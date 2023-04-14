package com.as.signup.service;

import com.alibaba.fastjson.JSONObject;
import com.as.signup.dto.SignupDTO;
import com.as.signup.entity.Classes;
import com.as.signup.entity.File;
import com.as.signup.entity.SignupRecord;
import com.as.signup.mapper.ClassesMapper;
import com.as.signup.mapper.FileMapper;
import com.as.signup.mapper.SignupRecordMapper;
import com.as.signup.properties.FileSystemProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.util.*;

@Service
public class SignupService {

    @Autowired
    private ClassesMapper classesMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private SignupRecordMapper signupRecordMapper;

    @Autowired
    private FileSystemProperties fileSystemProperties;

    /**
     * 获取所有课程信息
     */
    public List<Classes> getAllClasses() {
        return classesMapper.selectAll();
    }

    public List<Classes> getValidClasses(Integer period) {
        List<Classes> classes = classesMapper.selectPeriod(period);
        if (period == 6) {
            final int max = 27;
            classes.removeIf(c -> c.getCurrentNum() >= max);
        }
        return classes;
    }

    /**
     * 根据id获取课程信息
     */
    public Classes getClassesById(Integer id) {
        return classesMapper.selectByPrimaryKey(id);
    }

    /**
     * 插入file记录
     */
    public void insertFile(File file) {
        fileMapper.insert(file);
    }

    /**
     * 根据手机号查询报名信息
     */
    public List<SignupRecord> getSignupRecordByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }

        return signupRecordMapper.selectByMobile(mobile);
    }

    /**
     * 报名
     */
    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupDTO signupDTO) {
        Date now = new Date();
        // 参数校验
        if (StringUtils.isBlank(signupDTO.getMobile()) || CollectionUtils.isEmpty(signupDTO.getClasses())) {
            throw new RuntimeException("参数不完整");
        }

        // 锁课程信息
        List<Classes> classes = classesMapper.selectForUpdate(signupDTO.getClasses());

        // 校验
        for (Classes clazz : classes) {
            // 判断该手机号是否已经报名该课程
            List<SignupRecord> signupRecords = signupRecordMapper.selectByMobileAndClasses(signupDTO.getMobile(), clazz.getId());
            if (signupRecords.size() > 0) {
                throw new RuntimeException(clazz.getName() + ",已有报名记录");
            }

            // 判断报名课程是否还有名额
            if (clazz.getCurrentNum() >= clazz.getMaxNum()) {
                throw new RuntimeException(clazz.getName() + ",已超出限定名额【" + clazz.getMaxNum() + "】");
            }

            // 判断报名课程是否已截止报名
            if (clazz.getSignupEndtime().before(now)) {
                throw new RuntimeException(clazz.getName() + "，已过截止报名时间【" +
                        DateFormatUtils.format(clazz.getSignupEndtime(), "yyyy-MM-dd HH:mm:ss") + "】");
            }
        }

        signupDTO.setCreateTime(now);

        signupDTO.setFiles(JSONObject.toJSONString(Optional.ofNullable(
                signupDTO.getFileIds()).orElse(Collections.emptyList())) +
                JSONObject.toJSONString(Optional.ofNullable(
                        signupDTO.getFile2Ids()).orElse(Collections.emptyList())));
        // 校验通过后，生成报名记录
        for (Classes clazz : classes) {
            signupDTO.setClassesId(clazz.getId());
            signupRecordMapper.insertSelective(signupDTO);
            classesMapper.addSignupMember(clazz.getId());
        }
    }

    public XSSFWorkbook export(Integer classesId) {
        List<Integer> classesIds;
        if (classesId == 0) {
            classesIds = Arrays.asList(7,8,9,10,11);
        } else {
            classesIds = Collections.singletonList(classesId);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        for (Integer id : classesIds) {
            List<SignupRecord> signupRecords = signupRecordMapper.selectByClassesId(id);
            Classes classes = classesMapper.selectByPrimaryKey(id);
            createSheet(workbook, classes.getName(), transRows(signupRecords, classes));
        }

        return workbook;
    }

    private List<JSONObject> transRows(List<SignupRecord> signupRecords, Classes classes) {
        List<JSONObject> rs = new ArrayList<>();
        for (SignupRecord signupRecord : signupRecords) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(signupRecord));
            jsonObject.put("className", classes.getName());

            // jsonType
            String files = signupRecord.getFiles();
            files = files.substring(1, files.length() - 1);
            String[] split = files.split("]\\[");
            boolean isNotFree = split.length == 1;
            jsonObject.put("fileType", isNotFree ? "缴费凭证" : "续费凭证");

            // files
            String filesStr;
            if (isNotFree) {
                filesStr = split[0];
            } else {
                filesStr = split[1];
            }

            String[] fileStrArray = filesStr.split(",");
            List<String> paths = new ArrayList<>();
            for (int i = 0; i < fileStrArray.length; i++) {
                File file = fileMapper.selectByPrimaryKey(Integer.parseInt(fileStrArray[i]));
                String url = file.getUrl();
                String path = url.replaceAll(fileSystemProperties.getOuterPrefix(), "");
                paths.add(path);
            }
            jsonObject.put("files", String.join(",", paths));

            rs.add(jsonObject);
        }
        return rs;
    }

    private void createSheet(XSSFWorkbook workbook, String sheetName, List<JSONObject> signupRecords) {
        CreationHelper helper = workbook.getCreationHelper();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        Drawing<XSSFShape> drawing = sheet.createDrawingPatriarch();
        // 设置头
        XSSFRow header = sheet.createRow(0);
        for (int i = 0; i < headerName.length; i++) {
            XSSFCell cell = header.createCell(i);
            cell.setCellValue(headerName[i]);
        }

        for (int i = 1; i <= signupRecords.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            row.setHeightInPoints(100);
            JSONObject o = signupRecords.get(i - 1);
            for (int j = 0; j < headerKey.length; j++) {
                if (j < headerKey.length - 1) {
                    XSSFCell cell = row.createCell(j);
                    cell.setCellValue(o.getString(headerKey[j]));
                }
                // 凭证特殊处理
                else {
                    String[] filesStr = o.getString(headerKey[j]).split(",");
                    for (int k = 0; k < filesStr.length; k++) {
                        XSSFCell cell = row.createCell(j + k);
                        String path = filesStr[k];

                        try (FileInputStream fis = new FileInputStream(path)) {
                            int pictureIdx = workbook.addPicture(fis, path.contains("png") ? workbook.PICTURE_TYPE_PNG : workbook.PICTURE_TYPE_JPEG);

                            ClientAnchor anchor = helper.createClientAnchor();
                            // 图片插入坐标
                            anchor.setCol1(cell.getColumnIndex());
                            anchor.setCol2(cell.getColumnIndex() + 1);
                            anchor.setRow1(row.getRowNum());
                            anchor.setRow2(row.getRowNum() + 1);
                            // 插入图片
                            drawing.createPicture(anchor, pictureIdx);
                        } catch (Exception e) {
                            e.printStackTrace();
                            cell.setCellValue(o.getString(headerKey[j]));
                        }
                    }
                }
            }
        }
    }

    private static final String[] headerName = {"期数", "手机号码", "姓名", "单位", "职称", "所在地", "邮箱", "创建时间", "凭证类型", "凭证"};
    private static final String[] headerKey = {"className", "mobile", "name", "organization", "post", "area", "email", "createTime", "fileType", "files"};

}

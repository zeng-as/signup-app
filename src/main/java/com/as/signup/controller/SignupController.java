package com.as.signup.controller;

import com.as.signup.common.CommonEnums;
import com.as.signup.common.R;
import com.as.signup.dto.SignupDTO;
import com.as.signup.entity.Classes;
import com.as.signup.entity.File;
import com.as.signup.entity.SignupRecord;
import com.as.signup.properties.FileSystemProperties;
import com.as.signup.service.SignupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

@RestController
@Api(tags = "报名接口")
public class SignupController {

    @Autowired
    private SignupService signupService;

    @Autowired
    private FileSystemProperties fileSystemProperties;

    @ApiOperation(value = "获取所有课程信息")
    @GetMapping("/getRemainPlaces")
    public R<List<Classes>> getAllClasses() {
        return R.ok(signupService.getAllClasses());
    }

    @ApiOperation(value = "影像上传")
    @PostMapping("/fileUpload")
    public R<File> fileUpload(@RequestPart("files[]") MultipartFile[] files) {
        MultipartFile file = files[0];
        if (null == file) {
            return R.getInstance(CommonEnums.ResCode.EX_PARAM);
        }

        String nFileName;
        try {
            java.io.File uploadPath = new java.io.File(fileSystemProperties.getUploadPath());
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String prefix = UUID.randomUUID().toString();
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("");
            String suffix = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf("."));
            nFileName = prefix + suffix;
            java.io.File nFile = new java.io.File(fileSystemProperties.getUploadPath(), nFileName);
            FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(nFile.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return R.getInstance(CommonEnums.ResCode.EX_UPLOAD);
        }

        File f = new File();
        f.setUrl(fileSystemProperties.getOuterPrefix() + fileSystemProperties.getUploadPath() + "/" + nFileName);
        f.setCreateTime(new Date());
        signupService.insertFile(f);

        return R.ok(f);
    }

    @ApiOperation(value = "报名")
    @PostMapping("/signup")
    public R<Object> signup(@RequestBody SignupDTO signupDTO) {
        try {
            signupService.signup(signupDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return R.getInstance(CommonEnums.ResCode.EX_UPLOAD, null, e.getMessage());
        }
        return R.ok();
    }

    @ApiOperation(value = "查询报名记录")
    @PostMapping("/getSignupRecords")
    public R<List<Classes>> getSignupRecords(@RequestBody SignupRecord record) {
        List<SignupRecord> records = signupService.getSignupRecordByMobile(record.getMobile());
        List<Classes> rs = new ArrayList<>();
        for (SignupRecord signupRecord : records) {
            rs.add(signupService.getClassesById(signupRecord.getClassesId()));
        }
        return R.ok(rs);
    }

    @ApiOperation(value = "导出")
    @GetMapping("/export/{classesId}")
    public void export(@PathVariable(name = "classesId") Integer classesId, HttpServletResponse response) {
        final XSSFWorkbook workbook = signupService.export(classesId);
        try (final OutputStream os = response.getOutputStream()) {
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=export_data.xls");
            response.setCharacterEncoding("UTF-8");
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

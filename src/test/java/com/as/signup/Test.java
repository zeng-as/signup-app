package com.as.signup;

import cn.hutool.http.HttpUtil;
import com.as.signup.entity.File;
import com.as.signup.entity.SignupRecord;
import com.as.signup.mapper.FileMapper;
import com.as.signup.mapper.SignupRecordMapper;
import com.as.signup.service.SignupService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileCopyUtils;

import java.io.FileOutputStream;
import java.util.List;

/**
 * @author as
 * @date 2022/11/4
 * @desc
 */
@SpringBootTest
public class Test {
    @Autowired
    private SignupRecordMapper signupRecordMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private SignupService signupService;

    @org.junit.jupiter.api.Test
    public void t() {
        String baseDir = "F:\\signup";
        List<SignupRecord> signupRecords = signupRecordMapper.selectAll();
        for (SignupRecord signupRecord : signupRecords) {
            String files = signupRecord.getFiles();
            files = files.substring(1, files.length() - 1);
            String[] split = files.split("]\\[");
            StringBuilder sb = new StringBuilder();
            boolean rs = split.length == 1;
            String f;
            // 缴费凭证
            if (rs) {
                f = split[0];
            }
            // 优惠凭证
            else {
                f = split[1];
                sb.append("\t");
            }

            String[] split1 = f.split(",");
            for (int i = 0; i < split1.length; i++) {
                File file = fileMapper.selectByPrimaryKey(Integer.parseInt(split1[i]));
                String url = file.getUrl();
                String path = baseDir + "\\" + signupRecord.getClassesId() + "\\" + signupRecord.getMobile() + "-" + signupRecord.getName() + "\\";
                java.io.File fp = new java.io.File(path);
                if (!fp.exists()) {
                    fp.mkdirs();
                }

                String filePath = path + (rs ? "缴费凭证" : "优惠凭证") + (i + 1) + url.substring(url.lastIndexOf("."));
                HttpUtil.downloadFile(url, filePath);
                sb.append(filePath);
                if (i < split1.length - 1) {
                    sb.append(",");
                }
            }

            if (!rs) {
                sb.append("\t");
            }

            System.out.println(sb);
        }
        System.out.println(signupRecords.size());
    }

    @org.junit.jupiter.api.Test
    public void export() {
        XSSFWorkbook export = signupService.export(2);
        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\test.xlsx")) {
            export.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

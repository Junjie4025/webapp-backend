package edu.northeastern.ccwebapp.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class S3ServiceImpl {

    @Autowired
    private AmazonS3 s3client;
    private final static Logger logger = LogManager.getLogger(S3ServiceImpl.class);

    public S3ServiceImpl() {
        this.s3client = S3Config.amazonS3Client();
    }

    boolean uploadFile(String keyName, MultipartFile uploadFile, String bucketName) throws IllegalStateException, IOException {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") +
                    System.getProperty("file.separator") + uploadFile.getOriginalFilename());
            uploadFile.transferTo(convFile);
            /*convFile: path of the file to upload*/
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, convFile);
            s3client.putObject(putObjectRequest);
            return true;
        } catch (AmazonServiceException ase) {
            logger.error("Error :", ase);
            ase.printStackTrace();
            return false;
        } catch (AmazonClientException ace) {
            logger.error("Error :", ace);
            return false;
        }
    }

    void deleteFile(String keyName, String bucketName) {
        try {
            s3client.deleteObject(bucketName, keyName);
        } catch (AmazonServiceException ase) {
            logger.error("Error :", ase);
            ase.printStackTrace();
        } catch (AmazonClientException ace) {
            logger.error("Error :", ace);
            ace.getMessage();
        }

    }
}

package com.home.cloud.service;

import com.home.cloud.exception.BucketCreationException;
import com.home.cloud.exception.BucketNotFoundException;
import com.home.cloud.exception.folder.FolderException;
import com.home.cloud.model.AccountId;
import com.home.cloud.model.BucketModel;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.util.List;

@Service
public class BucketService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final MinioClient minioClient;

    public BucketService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBucket(String username) {

        String bucket_name = username + "bucket";

        AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer account_id = id.getAccount_id();

        if (!isBucketNameValid(bucket_name)) {
            throw new IllegalArgumentException("Invalid bucket name: " + bucket_name);
        }
        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                CallableStatement cs = connection.prepareCall("call sp_account_bucket(?,?)");

                cs.setString(1, bucket_name);
                cs.setInt(2, account_id);
                cs.execute();
                return null;
            });
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(bucket_name)
                            .build()
            );
        } catch (Exception e) {
            throw new BucketCreationException("I cannot create the bucket", e);
        }
    }

    private boolean isBucketNameValid(String bucketName) {
        return bucketName.length() >= 3 && bucketName.length() <= 63 &&
                bucketName.matches("^[a-z0-9.-]+$") &&
                !bucketName.contains("..") &&
                !bucketName.startsWith("-") &&
                !bucketName.endsWith("-");
    }

    public boolean isBucketExists(String bucketName) {
        try {
            minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
            return true;
        } catch (Exception e) {
            throw new BucketNotFoundException("Bucket not found: " + e);
        }
    }

    public List<BucketModel> getBucket() {
        AccountId id = (AccountId) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer account_id = id.getAccount_id();

        try {
            return jdbcTemplate.query(
                    "select * from f_get_bucket(?)",
                    new Object[]{account_id},
                    (rs, rowNum) -> {
                        BucketModel bucket = new BucketModel();
                        bucket.setBucket_name(rs.getString("out_bucket_name"));
                        return bucket;
                    }
            );
        } catch (Exception e) {
            throw new FolderException("Error getting data", e);
        }
    }

}
package ar.edu.utn.frba.dds.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3Service{

  @Value("${spring.destination.folder}")
  private String destinationFolder;

  @Autowired
  private S3Client s3Client;

  @Autowired
  private S3Presigner s3Presigner;

  public String createBucket(String bucketName) {
    CreateBucketResponse response = this.s3Client.createBucket(bucketBuilder -> bucketBuilder.bucket(bucketName));
    return "Bucket creado en la ubicación: " + response.location();
  }

  public String checkIfBucketExist(String bucketName) {
    try {
      this.s3Client.headBucket( headBucket -> headBucket.bucket(bucketName) );
      return "El bucket " + bucketName + " si existe.";
    } catch(S3Exception exception){
      return "El bucket " + bucketName + " no existe.";
    }
  }

  public List<String> getAllBuckets() {
    ListBucketsResponse bucketsResponse = this.s3Client.listBuckets();

    if(bucketsResponse.hasBuckets()){
      return bucketsResponse.buckets()
          .stream()
          .map(Bucket::name)
          .toList();
    } else {
      return List.of();
    }
  }

  public String uploadFile(String bucketName, String key, Path fileLocation) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    this.s3Client.listBuckets().buckets().forEach(b -> System.out.println(b.name()));

    PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest, fileLocation);
    String url = "https://" + bucketName + ".s3.us-east-2.amazonaws.com/" + key;

    Boolean succesful = putObjectResponse.sdkHttpResponse().isSuccessful();
    if (succesful) {
      return url;
    } else {
      throw new RuntimeException("Error al subir el archivo");
    }
  }

  public void downloadFile(String bucket, String key) throws IOException {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(getObjectRequest);

    String fileName;

    if(key.contains("/")){
      fileName = key.substring( key.lastIndexOf("/") );
    } else {
      fileName = key;
    }

    String filePath = Paths.get("src", "main", "resources", "static", fileName).toString();

    File file = new File(filePath);
    file.getParentFile().mkdirs();

    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(objectBytes.asByteArray());
    } catch (IOException e) {
      throw new IOException("Error al descargar el archivo: " + e.getCause());
    }
  }

  public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(duration)
        .putObjectRequest(putObjectRequest)
        .build();

    PresignedPutObjectRequest presignedRequest = this.s3Presigner.presignPutObject(presignRequest);
    URL presignedUrl = presignedRequest.url();

    return presignedUrl.toString();
  }

  public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(duration)
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = this.s3Presigner.presignGetObject(presignRequest);
    URL presignedUrl = presignedRequest.url();

    return presignedUrl.toString();
  }
}

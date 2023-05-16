package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.ServerSideEncryption;
import io.minio.ServerSideEncryptionCustomerKey;
import io.minio.UploadObjectArgs;
import okhttp3.OkHttpClient;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		upload();
		SpringApplication.run(DemoApplication.class, args);
	}

	public static void upload() {

		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[] {};
					}
				}
		};

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
			newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
			newBuilder.hostnameVerifier((hostname, session) -> true);
			OkHttpClient httpClient = newBuilder.build();

			MinioClient minioClient = MinioClient.builder()
					.endpoint("https://rocky1:9000")
					.credentials("KxZN2etdmzQFKZ7F", "MSyP7RsByE1iXZQOdlJCOz2LYPVVbWz7")
					.httpClient(httpClient)
					.build();

			// KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			// keyGenerator.init(123456678);
			// SecretKey originalKey = keyGenerator.generateKey();
			// System.out.println("originalKey:" + originalKey);
			// ServerSideEncryptionCustomerKey ssec = new ServerSideEncryptionCustomerKey(originalKey);

			ServerSideEncryptionCustomerKey srcSsec = new ServerSideEncryptionCustomerKey(
					new SecretKeySpec(
							"01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8), "AES"));

			ServerSideEncryption sse = new ServerSideEncryptionCustomerKey(
					new SecretKeySpec(
							"12345678912345678912345678912345".getBytes(StandardCharsets.UTF_8), "AES"));

			// List<ComposeSource> sources = new ArrayList<>();
			// sources.add(
			// ComposeSource.builder()
			// .bucket("my-bucketname")
			// .object("my-objectname-one")
			// .ssec(srcSsec)
			// .build());
			// sources.add(
			// ComposeSource.builder()
			// .bucket("my-bucketname")
			// .object("my-objectname-two")
			// .ssec(srcSsec)
			// .build());

			// minioClient.composeObject(
			// ComposeObjectArgs.builder()
			// .bucket("my-destination-bucket")
			// .object("my-destination-object")
			// .sources(sources)
			// .sse(sse)
			// .build());
			// System.out.println("Object Composed successfully");

			File initialFile = new File("src/text.txt");
			InputStream targetStream = new FileInputStream(initialFile);
			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket("my-bucketname")
							.object("my-objectname")
							.stream(targetStream, initialFile.length(), -1)
							.sse(sse)
							.build());

			// minioClient.uploadObject(
			// UploadObjectArgs.builder()
			// .bucket("my-bucketname")
			// .object("my-objectname")
			// .filename("my-video.avi")
			// .contentType("video/mp4")
			// .sse(null)
			// .build());

		} catch (Exception e) {
			e.printStackTrace();// debug
		}
	}

	// try (InputStream stream =
	// * minioClient.getObject(
	// * GetObjectArgs.builder()
	// * .bucket("my-bucketname")
	// * .object("my-objectname")
	// * .offset(offset)
	// * .length(len)
	// * .ssec(ssec)
	// * .build()
	// * ) {
	// * // Read data from stream
	// * }

}

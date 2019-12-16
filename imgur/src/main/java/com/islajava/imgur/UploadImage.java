package com.islajava.imgur;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UploadImage {

	public static void main(String[] args) {
		String imageDir = "";
		String clientId = "";
		imgUr(imageDir, clientId);
	}

	public static void imgUr(String imageDir, String clientID) {
		// create needed strings
		String address = "https://api.imgur.com/3/upload";

		// Create HTTPClient and post
		CloseableHttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost post = new HttpPost(address);

		// create base64 image
		BufferedImage image = null;
		File file = new File(imageDir);

		try {
			// read image
			image = ImageIO.read(file);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ImageIO.write(image, "png", byteArray);
			byte[] byteImage = byteArray.toByteArray();
			String dataImage = new Base64().encodeAsString(byteImage);

			// add header
			post.addHeader("Authorization", "Client-ID " + clientID);
			// add image
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("image", dataImage));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// execute
			HttpResponse response = client.execute(post);

			String jsonString = EntityUtils.toString(response.getEntity());

			ImgUrResponse responseJson = new ObjectMapper().readValue(jsonString, ImgUrResponse.class);
			System.out.println(responseJson.getData().getLink());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

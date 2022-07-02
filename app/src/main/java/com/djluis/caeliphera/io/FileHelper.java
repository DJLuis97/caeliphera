package com.djluis.caeliphera.io;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {
	@Nullable
	public static String getToken (FileInputStream fis) {
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader    br  = new BufferedReader(isr);
		try {
			String token = br.readLine();
			br.close();
			isr.close();
			return token;
		} catch (IOException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "FileHelper@getToken -> " + exception.getMessage());
			return null;
		}
	}

	public static boolean storeToken (FileOutputStream fos, String token) {
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		try {
			osw.write(token);
			osw.flush();
			osw.close();
			return true;
		} catch (IOException exception) {
			Log.e("(╯°□°）╯︵ ┻━┻ |>", "FileHelper@getToken -> " + exception.getMessage());
			return false;
		}
	}
}
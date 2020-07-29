package uk.co.urbanfortress.aws.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.ini4j.Wini;
import org.springframework.stereotype.Component;

import uk.co.urbanfortress.aws.ApplicationException;
import uk.co.urbanfortress.aws.CredentialsWriter;

@Component
public class DefaultCredentialsWriter implements CredentialsWriter {
	public void writeAllCredentials(String credentialsFile, List<Properties> credentailsCollection) {
		Wini ini;
		try {
			ini = new Wini(new File(credentialsFile));
		} catch (IOException e) {
			throw new ApplicationException("can not open credentials file");
		}

		for(Properties credentials: credentailsCollection) {
			writeCredentials(ini, credentials);
		}
		storeIni(ini);
	}
	
	private void writeCredentials(final Wini ini, final Properties properties) {
		final String sectionName = getSectionName(properties);

		properties.forEach((key, value) -> {
			if (!value.equals("")) {
				ini.put(sectionName, key.toString(), value.toString());
			}
		});
		ini.put(sectionName, "timestamp", formattedDate());
	}

	private void storeIni(final Wini ini) {
		try {
			ini.store();
		} catch (IOException e) {
			new ApplicationException("Can not write credentials file");
		}
	}

	private String getSectionName(final Properties properties) {
		final String sectionName = properties.entrySet().stream().filter((mapEntry) -> mapEntry.getValue().equals(""))
				.findFirst().get().getKey().toString().replace("[", "").replace("]", "");
		return sectionName;
	}

	private String formattedDate() {
		Date date = new Date(System.currentTimeMillis());
		String formattedDate = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(date);
		return formattedDate;
	}
}

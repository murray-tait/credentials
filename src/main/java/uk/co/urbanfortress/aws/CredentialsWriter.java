package uk.co.urbanfortress.aws;

import java.util.List;
import java.util.Properties;

public interface CredentialsWriter {
	void writeAllCredentials(String credentialsFile, List<Properties> credentailsCollection);
}
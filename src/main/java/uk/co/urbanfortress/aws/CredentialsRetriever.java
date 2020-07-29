package uk.co.urbanfortress.aws;

import java.util.List;
import java.util.Properties;

public interface CredentialsRetriever {
	List<Properties> retrieveAllCredentials(String username, String password, String portalUrl);
}

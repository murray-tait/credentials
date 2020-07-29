package uk.co.urbanfortress.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "job.autorun", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AutomationRunner implements CommandLineRunner {

	private CredentialsRetriever credentialsRetriever;
	private CredentialsWriter credentialsWriter;
	private CommandLineParser parser;

	public AutomationRunner(CredentialsRetriever credentialsRetriever, CredentialsWriter credentialsWriter) {
		super();
		this.credentialsRetriever = credentialsRetriever;
		this.credentialsWriter = credentialsWriter;
		parser = new DefaultParser();
	}

	@Override
	public void run(String... args) throws Exception {
		Option usernameOption = new Option("u", "username", true, "sso username");
		Option portalUrlOption = new Option("s", "ssoportal", true, "sso portal url");
		Option credentialsFileOption = new Option("c", "credentials", true, "aws credentials file");
		Option passwordOption = new Option("p", "password", true, "sso password");
		passwordOption.setRequired(false);

		Options options = new Options().addOption(usernameOption).addOption(portalUrlOption)
				.addOption(credentialsFileOption).addOption(passwordOption);

		CommandLine cmd = parser.parse(options, args);

		String password = cmd.getOptionValue(passwordOption.getOpt());
		if (password == null) {
			password = getPassword();
		}
		String portalUrl = cmd.getOptionValue(portalUrlOption.getOpt());
		String username = cmd.getOptionValue(usernameOption.getOpt());
		String credentialsFile = cmd.getOptionValue(credentialsFileOption.getOpt());

		List<Properties> credentailsCollection = credentialsRetriever.retrieveAllCredentials(username, password,
				portalUrl);

		credentialsWriter.writeAllCredentials(credentialsFile, credentailsCollection);
	}

	private String getPassword() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String password = reader.readLine();
		return password;
	}
}

package uk.co.urbanfortress.aws;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
    usernameOption.setRequired(true);
    Option portalUrlOption = new Option("s", "ssoportal", true, "sso portal url");
    portalUrlOption.setRequired(true);
    Option credentialsFileOption = new Option("c", "credentials", true, "aws credentials file");
    credentialsFileOption.setRequired(true);
    Option pipedPasswordOption = new Option("P", "pipedPassword", false, "piped sso password");
    pipedPasswordOption.setRequired(false);
    Option passwordOption = new Option("p", "password", false, "sso password");
    passwordOption.setRequired(false);
    Options options = new Options().addOption(usernameOption).addOption(portalUrlOption)
        .addOption(credentialsFileOption).addOption(passwordOption).addOption(pipedPasswordOption);

    try {
      CommandLine cmd = parser.parse(options, args);

      String password;
      if (cmd.hasOption(pipedPasswordOption.getOpt())) {
        password = getPipedPassword();
      } else if (cmd.hasOption(passwordOption.getOpt())) {
        password = cmd.getOptionValue(passwordOption.getOpt());
      } else {
        password = getPromptedPassword();
      }

      String portalUrl = cmd.getOptionValue(portalUrlOption.getOpt());
      String username = cmd.getOptionValue(usernameOption.getOpt());
      String credentialsFile = cmd.getOptionValue(credentialsFileOption.getOpt());

      List<Properties> credentailsCollection = credentialsRetriever.retrieveAllCredentials(username, password,
          portalUrl);

      credentialsWriter.writeAllCredentials(credentialsFile, credentailsCollection);
    } catch (ParseException exp) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("credentials", options);
    } catch (ApplicationException exp) {
      System.err.print(exp.getMessage());
      throw exp;
    }
  }

  private String getPipedPassword() {
    String result = "";

    Scanner input = new Scanner(System.in);
    if (input.hasNext()) {
      result = input.nextLine();

    }
    input.close();

    return result;
  }

  private String getPromptedPassword() throws IOException {
    Console console = System.console();
    char[] passwordArray = console.readPassword("Enter your secret password: ");
    return new String(passwordArray);
  }
}

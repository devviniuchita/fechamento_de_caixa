package com.controle.fechamentocaixa.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Google google = new Google();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }

    public Google getGoogle() { return google; }
    public void setGoogle(Google google) { this.google = google; }

    public static class Jwt {
        private String secret;
        private int expiration;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }

        public int getExpiration() { return expiration; }
        public void setExpiration(int expiration) { this.expiration = expiration; }
    }

    public static class Google {
        private Drive drive = new Drive();

        public Drive getDrive() { return drive; }
        public void setDrive(Drive drive) { this.drive = drive; }

        public static class Drive {
            private String credentialsFile;
            private String backupFolderId;
            private String tokensFolder;
            private String applicationName;

            public String getCredentialsFile() { return credentialsFile; }
            public void setCredentialsFile(String credentialsFile) { this.credentialsFile = credentialsFile; }

            public String getBackupFolderId() { return backupFolderId; }
            public void setBackupFolderId(String backupFolderId) { this.backupFolderId = backupFolderId; }

            public String getTokensFolder() { return tokensFolder; }
            public void setTokensFolder(String tokensFolder) { this.tokensFolder = tokensFolder; }

            public String getApplicationName() { return applicationName; }
            public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
        }
    }
}



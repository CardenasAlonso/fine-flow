package pe.edu.fineflow.identity.infrastructure.config;

import io.r2dbc.spi.ConnectionFactoryOptions;
import oracle.r2dbc.OracleR2dbcOptions;
import oracle.security.pki.OraclePKIProvider;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;
import java.time.Duration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class OracleR2dbcConfig {

    static {
        Security.addProvider(new OraclePKIProvider());
    }

    @Bean
    public R2dbcConnectionDetails r2dbcConnectionDetails() {
        String walletDir = System.getenv("TNS_ADMIN");
        if (walletDir == null || walletDir.isEmpty())
            walletDir = System.getenv("ORACLE_WALLET_DIR");
        if (walletDir == null || walletDir.isEmpty())
            walletDir = System.getProperty("oracle.net.tns_admin", "");

        String user = System.getenv("ORACLE_USER");
        if (user == null || user.isEmpty()) user = "FINEFLOW_APP";
        String password = System.getenv("ORACLE_PASSWORD");
        if (password == null || password.isEmpty()) password = "FineFlow_2025!";

        String tnsAlias = System.getenv("ORACLE_TNS_ALIAS");
        if (tnsAlias == null || tnsAlias.isEmpty()) tnsAlias = "FREEPDB1_high";

        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder()
                .option(DRIVER, "oracle")
                .option(OracleR2dbcOptions.DESCRIPTOR, tnsAlias)
                .option(USER, user)
                .option(PASSWORD, password)
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(30));

        if (!walletDir.isEmpty()) {
            builder.option(OracleR2dbcOptions.TNS_ADMIN, walletDir);
            builder.option(OracleR2dbcOptions.TLS_WALLET_LOCATION,
                    "(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=" + walletDir + ")))");
        }

        ConnectionFactoryOptions options = builder.build();

        return () -> options;
    }
}

package con.domingosuarez.boot.social.google;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Domingo Suarez Torres
 * @since 1.0.0
 */
@Data
@ConfigurationProperties("spring.social.google")
public class GoogleProperties {
  /**
   * Application id.
   */
  private String appId;

  /**
   * Application secret.
   */
  private String appSecret;
}

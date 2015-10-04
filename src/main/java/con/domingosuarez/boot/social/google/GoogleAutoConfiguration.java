package con.domingosuarez.boot.social.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.web.servlet.View;

import static java.util.Optional.ofNullable;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Social connectivity with
 * Google.
 *
 * @author Domingo Suarez Torres
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({SocialConfigurerAdapter.class, GoogleConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.social.google", value = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class GoogleAutoConfiguration {

  @Configuration
  @EnableSocial
  @EnableConfigurationProperties(GoogleProperties.class)
  @ConditionalOnWebApplication
  protected static class GoogleAutoConfigurationAdapter extends SocialConfigurerAdapter {

    @Autowired
    private GoogleProperties properties;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer configurer, Environment environment) {
      configurer.addConnectionFactory(new GoogleConnectionFactory(
        this.properties.getAppId(),
        this.properties.getAppSecret()));
    }

    @Bean
    @ConditionalOnMissingBean(Google.class)
    @Scope(value = "request", proxyMode = INTERFACES)
    public Google google(ConnectionRepository repository) {
      return ofNullable(repository.findPrimaryConnection(Google.class))
        .map(c -> c.getApi())
        .orElse(new GoogleTemplate());
    }

    @Bean(name = {"connect/googleConnect", "connect/googleConnected"})
    @ConditionalOnProperty(prefix = "spring.social", value = "auto-connection-views")
    public View googleConnectView() {
      return new GenericConnectionStatusView("google", "Google");
    }

  }

}

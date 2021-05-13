package constants;

public final class Constants {
  private Constants() {}

  // Kafka constants
  public static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";
  public static final String GROUP_ID = "tweets-group";

  public static final String ELASTIC_SEARCH_HOSTNAME = System.getenv("ELASTIC_SEARCH_HOSTNAME");
  public static final String ELASTIC_SEARCH_USERNAME = System.getenv("ELASTIC_SEARCH_USERNAME");
  public static final String ELASTIC_SEARCH_PASSWORD = System.getenv("ELASTIC_SEARCH_PASSWORD");
}

package constants;

public final class Constants {
  private Constants() {}

  public static final String ELASTIC_SEARCH_HOSTNAME = System.getenv("ELASTIC_SEARCH_HOSTNAME");
  public static final String ELASTIC_SEARCH_USERNAME = System.getenv("ELASTIC_SEARCH_USERNAME");
  public static final String ELASTIC_SEARCH_PASSWORD = System.getenv("ELASTIC_SEARCH_PASSWORD");
}

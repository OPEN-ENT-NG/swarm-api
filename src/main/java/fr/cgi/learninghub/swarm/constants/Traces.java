package fr.cgi.learninghub.swarm.constants;

public class Traces {
    public static final String SEND_MAIL = "SEND_MAIL";
    public static final String PATCH_STATUS_SERVICE = "PATCH_STATUS_SERVICE";
    public static final String RESET_SERVICE = "RESET_SERVICE";
    public static final String UPDATE_SERVICE = "UPDATE_SERVICE";
    public static final String DELETE_SERVICE = "DELETE_SERVICE";
    public static final String CREATE_SERVICE = "CREATE_SERVICE";

    private Traces() {
        throw new IllegalStateException("Traces is a utility class and should not be instantiated");
    }
}

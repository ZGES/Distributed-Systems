public enum Services {

    LOAD("load"),
    PERSON("person"),
    SATELlITE("satellite");

    private String service;

    Services(String service){
        this.service = service;
    }

    public String getService() {
        return service;
    }
}


package tretnt.vn.weather.model;

public class Weather {


    private double id;

    private String main;

    private String description;

    private String icon;

    /**
     *
     * @return
     *     The id
     */
    public double getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(double id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The main
     */
    public String getMain() {
        return main;
    }

    /**
     *
     * @param main
     *     The main
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     *
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     *
     * @param icon
     *     The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconUrl(){
        return "http://openweathermap.org/img/w/" + icon +".png";
    }

}

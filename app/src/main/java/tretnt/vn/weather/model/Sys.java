
package tretnt.vn.weather.model;

public class Sys {

    private double type;

    private double id;

    private double message;

    private String country;

    private double sunrise;

    private double sunset;

    /**
     * 
     * @return
     *     The type
     */
    public double getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(double type) {
        this.type = type;
    }

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
     *     The message
     */
    public double getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(double message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The sunrise
     */
    public double getSunrise() {
        return sunrise;
    }

    /**
     * 
     * @param sunrise
     *     The sunrise
     */
    public void setSunrise(double sunrise) {
        this.sunrise = sunrise;
    }

    /**
     * 
     * @return
     *     The sunset
     */
    public double getSunset() {
        return sunset;
    }

    /**
     * 
     * @param sunset
     *     The sunset
     */
    public void setSunset(double sunset) {
        this.sunset = sunset;
    }


}

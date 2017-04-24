package com.carl.interesting.common.session;

/**
 * custom session class
 * 
 * @author Carl Liu
 * @version [version, 12 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class CustomSession {
    /**
     * session last access time
     */
    private Long lastTime;
    
    /**
     * session language
     */
    private String lang;
    
    /**
     * @return returns lastTime
     */
    public Long getLastTime() {
        return lastTime;
    }
    
    /**
     * @param assgin values to lastTime
     */
    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }
    
    /**
     * @return returns lang
     */
    public String getLang() {
        return lang;
    }
    
    /**
     * @param assgin values to lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }
    
    /**
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lang == null) ? 0 : lang.hashCode());
        result = prime * result
                + ((lastTime == null) ? 0 : lastTime.hashCode());
        return result;
    }
    
    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomSession other = (CustomSession) obj;
        if (lang == null) {
            if (other.lang != null)
                return false;
        }
        else if (!lang.equals(other.lang))
            return false;
        if (lastTime == null) {
            if (other.lastTime != null)
                return false;
        }
        else if (!lastTime.equals(other.lastTime))
            return false;
        return true;
    }
    
    /**
     * @return
     */
    @Override
    public String toString() {
        return "CustomSession [lastTime=" + lastTime + ", lang=" + lang + "]";
    }
}

package com.carl.interesting.common.constants;

/**
 * 标识的枚举类（yes，no，success，fail等）
 * 
 * @author Yangbin Zhang
 * @version [version, 2 Aug 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public enum FlagEnum {
    /**
     * yes
     */
    YES {
        public String getString() {
            return "yes";
        }
    },
    /**
     * no
     */
    NO {
        public String getString() {
            return "no";
        }
    };
    public abstract String getString();
}

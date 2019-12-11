package com.dixon.phoneshare.bean;

public class SelectItem<Bean> {

    Bean bean;

    boolean hasSelect;

    public SelectItem(Bean bean, boolean hasSelect) {
        this.bean = bean;
        this.hasSelect = hasSelect;
    }

    public SelectItem(Bean bean) {
        this(bean, false);
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }

    public boolean isHasSelect() {
        return hasSelect;
    }

    public void setHasSelect(boolean hasSelect) {
        this.hasSelect = hasSelect;
    }

    @Override
    public String toString() {
        return "SelectItem{" +
                "bean=" + bean.toString() +
                ", hasSelect=" + hasSelect +
                '}';
    }
}

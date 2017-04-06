package com.pacificcycle.data;

public class Product
{
    String cProductItemNo;
    String cQty;
    String cShipDetailNo;
    String Seq;
    String ShipQty;

    private String modelType;
    private String modelColor;
    private String model;

    public Product(String seq, String shipDetailNo, String productItemNo, String qty, String shipQty, String modelType, String model, String modelColor, String cProductItemNo)
    {
        this.Seq = seq;
        this.cShipDetailNo = shipDetailNo;
        this.cProductItemNo = productItemNo;
        this.cQty = qty;
        this.ShipQty = shipQty;
        this.cProductItemNo = cProductItemNo;
        this.modelType = modelType;
        this.modelColor = modelColor;
        this.model = model;
    }

    public String getProductItemNo()
    {
        return this.cProductItemNo;
    }

    public String getQty()
    {
        return this.cQty;
    }

    public String getSeq()
    {
        return this.Seq;
    }

    public String getShipDetailNo()
    {
        return this.cShipDetailNo;
    }

    public String getShipQty()
    {
        return this.ShipQty;
    }

    public String getModelType()
    {
        return this.modelType;

    }

    public String getModelColor()
    {
        return this.modelColor;

    }

    public String getModel()
    {
        return this.model;

    }
}

package com.pacificcycle.data;

public class ProductItem
{
    String cBBNo;
    String cProductNo;
    String cShipNo;
    String Seq;
    private String cModelNo;

    public ProductItem(String seq, String productNo, String bbNo, String shipNo, String cModelNo)
    {
        this.Seq = seq;
        this.cProductNo = productNo;
        this.cBBNo = bbNo;
        this.cShipNo = shipNo;
        this.cModelNo = cModelNo;
    }

    public String getBBNo()
    {
        return this.cBBNo;
    }

    public String getProductNo()
    {
        return this.cProductNo;
    }

    public String getSeq()
    {
        return this.Seq;
    }

    public String getShipNo()
    {
        return this.cShipNo;
    }

    public CharSequence getModelNo()
    {
        return this.cModelNo;

    }
}

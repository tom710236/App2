package com.pacificcycle.data;

public class Shipment
{
    String cCreateDT;
    String cShipNo;
    String cShipStatus;
    private String customer;

    public Shipment(String createDT, String shipNo, String shipStatus, String customer)
    {
        this.cCreateDT = createDT;
        this.cShipNo = shipNo;
        this.cShipStatus = shipStatus;
        this.customer = customer;
    }

    public String getCreateDT()
    {
        return this.cCreateDT;
    }

    public String getShipNo()
    {
        return this.cShipNo;
    }

    public String getShipStatus()
    {
        return this.cShipStatus;
    }

    public String getCustomer()
    {
      return this.customer;
    }
}

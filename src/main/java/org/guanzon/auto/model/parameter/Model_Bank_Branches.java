/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.model.parameter;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Model_Bank_Branches implements GEntity{
    final String XML = "Model_Bank_Branches.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Bank_Branches(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
    }
    
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Gets the column index name.
     *
     * @param fnValue - column index number
     * @return column index name
     */
    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    /**
     * Gets the column index number.
     *
     * @param fsValue - column index name
     * @return column index number
     */
    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the total number of column.
     *
     * @return total number of column
     */
    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public String getTable() {
        return "banks_branches";
    }

    /**
     * Gets the value of a column index number.
     *
     * @param fnColumn - column index number
     * @return object value
     */
    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the value of a column index name.
     *
     * @param fsColumn - column index name
     * @return object value
     */
    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets column value.
     *
     * @param fnColumn - column index number
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(int fnColumn, Object foValue) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Sets column value.
     *
     * @param fsColumn - column index name
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(String fsColumn, Object foValue) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, fsColumn), foValue);
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    /**
     * Set the edit mode of the entity to new.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;

        //replace with the primary key column info
        setBrBankID(MiscUtil.getNextCode(getTable(), "sBrBankID", true, poGRider.getConnection(), poGRider.getBranchCode()+"BK"));

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsValue - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " sBrBankID = " + SQLUtil.toSQL(fsValue));

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }

                pnEditMode = EditMode.UPDATE;

                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Save the entity.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject saveRecord() {
        String lsExclude = "sBankName»sBankType»sTownName»sProvName»sProvIDxx»xAddressx";
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setBrBankID(MiscUtil.getNextCode(getTable(), "sBrBankID", true, poGRider.getConnection(), poGRider.getBranchCode()+"BK"));
//                setEntryBy(poGRider.getUserID());
//                setEntryDte(poGRider.getServerDate());
                setModified(poGRider.getUserID());
                setModifiedDte(poGRider.getServerDate());
                
                lsSQL = MiscUtil.makeSQL(this, lsExclude);

                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {
                Model_Bank_Branches loOldEntity = new Model_Bank_Branches(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getBrBankID());

                if ("success".equals((String) loJSON.get("result"))) {
                    setModified(poGRider.getUserID());
                    setModifiedDte(poGRider.getServerDate());
                    
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sBrBankID = " + SQLUtil.toSQL(this.getBrBankID()),lsExclude);

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
                        }
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }

        return poJSON;
    }

    /**
     * Prints all the public methods used<br>
     * and prints the column names of this entity.
     */
    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        try {
            int lnRow = poEntity.getMetaData().getColumnCount();

            System.out.println("--------------------------------------------------------------------");
            System.out.println("ENTITY COLUMN INFO");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Total number of columns: " + lnRow);
            System.out.println("--------------------------------------------------------------------");

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++) {
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR
                        || poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR) {

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }

    }
    
    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "");
    }
    
    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SQL Select Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this);
    }
    
    public String getSQL(){
        return    "   SELECT "                                         
                + "   a.sBrBankID "                                    
                + " , a.sBrBankNm "                                    
                + " , a.sBrBankCd "                                    
                + " , a.sBankIDxx "                                    
                + " , a.sContactP "                                    
                + " , a.sAddressx "                                    
                + " , a.sTownIDxx "                                    
                + " , a.sZippCode "                                    
                + " , a.sTelNoxxx "                                    
                + " , a.sFaxNoxxx "                                    
                + " , a.cRecdStat "                                    
                + " , a.sModified "                                    
                + " , a.dModified "                                    
                + " , b.sBankName "                                      
                + " , b.sBankType "                                  
                + " , c.sTownName "                                    
                + " , d.sProvName "                                   
                + " , d.sProvIDxx "                                  
                + " , UPPER(CONCAT(a.sAddressx,' ', c.sTownName, ', ', d.sProvName)) AS xAddressx"                                    
                + " FROM banks_branches a "                            
                + " LEFT JOIN banks b ON b.sBankIDxx = a.sBankIDxx "   
                + " LEFT JOIN towncity c ON c.sTownIDxx = a.sTownIDxx "
                + " LEFT JOIN province d ON d.sProvIDxx = c.sProvIDxx ";  
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBrBankID(String fsValue) {
        return setValue("sBrBankID", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getBrBankID() {
        return (String) getValue("sBrBankID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBrBankNm(String fsValue) {
        return setValue("sBrBankNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBrBankNm() {
        return (String) getValue("sBrBankNm");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBrBankCd(String fsValue) {
        return setValue("sBrBankCd", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBrBankCd() {
        return (String) getValue("sBrBankCd");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankID(String fsValue) {
        return setValue("sBankIDxx", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getBankID() {
        return (String) getValue("sBankIDxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setContactP(String fsValue) {
        return setValue("sContactP", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getContactP() {
        return (String) getValue("sContactP");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTownID(String fsValue) {
        return setValue("sTownIDxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTownID() {
        return (String) getValue("sTownIDxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setZippCode(String fsValue) {
        return setValue("sZippCode", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getZippCode() {
        return (String) getValue("sZippCode");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTelNo(String fsValue) {
        return setValue("sTelNoxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTelNo() {
        return (String) getValue("sTelNoxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFaxNo(String fsValue) {
        return setValue("sFaxNoxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getFaxNo() {
        return (String) getValue("sFaxNoxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setRecdStat(String fsValue) {
        return setValue("cRecdStat", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getRecdStat() {
        return (String) getValue("cRecdStat");
    }
    
    /**
     * Sets record as active.
     *
     * @param fbValue
     * @return result as success/failed
     */
    public JSONObject setActive(boolean fbValue) {
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }

    /**
     * @return If record is active.
     */
    public boolean isActive() {
        return ((String) getValue("cRecdStat")).equals("1");
    }
    
//    /**
//     * Description: Sets the Value of this record.
//     *
//     * @param fsValue
//     * @return result as success/failed
//     */
//    public JSONObject setEntryBy(String fsValue) {
//        return setValue("sEntryByx", fsValue);
//    }
//
//    /**
//     * @return The Value of this record.
//     */
//    public String getEntryBy() {
//        return (String) getValue("sEntryByx");
//    }
//    
//    /**
//     * Sets the date and time the record was modified.
//     *
//     * @param fdValue
//     * @return result as success/failed
//     */
//    public JSONObject setEntryDte(Date fdValue) {
//        return setValue("dEntryDte", fdValue);
//    }
//
//    /**
//     * @return The date and time the record was modified.
//     */
//    public Date getEntryDte() {
//        return (Date) getValue("dEntryDte");
//    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModified(String fsValue) {
        return setValue("sModified", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getModified() {
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setModifiedDte(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDte() {
        return (Date) getValue("dModified");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankName(String fsValue) {
        return setValue("sBankName", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankName() {
        return (String) getValue("sBankName");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankType(String fsValue) {
        return setValue("sBankType", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankType() {
        return (String) getValue("sBankType");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTownName(String fsValue) {
        return setValue("sTownName", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTownName() {
        return (String) getValue("sTownName");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setProvName(String fsValue) {
        return setValue("sProvName", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getProvName() {
        return (String) getValue("sProvName");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setProvID(String fsValue) {
        return setValue("sProvIDxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getProvID() {
        return (String) getValue("sProvIDxx");
    }
    
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setXAddress(String fsValue) {
        return setValue("xAddressx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getXAddress() {
        return (String) getValue("xAddressx");
    }
}

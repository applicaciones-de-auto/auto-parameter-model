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
public class Model_Vehicle_Model_Engine_Pattern implements GEntity {

    final String XML = "Model_Vehicle_Model_Engine_Pattern.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Vehicle_Model_Engine_Pattern(GRider foValue) {
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
            poEntity.updateInt("nEngnLenx", 0);

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
        return "vehicle_model_engine_pattern";
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
        //setEntryNo(Integer.valueOf(MiscUtil.getNextCode(getTable(), "nEntryNox", false, poGRider.getConnection(), "")));
        setEntryNo(Integer.valueOf(MiscUtil.getNextCode(getTable(), "nEntryNox", false, poGRider.getConnection(), "")));
        //setEntryNo(MiscUtil.getNextCode(getTable(), "nEntryNox", false, null, null));
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsCondition - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsCondition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        poJSON = new JSONObject();
//
//        String lsSQL = MiscUtil.makeSelect(this);
//
//        //replace the condition based on the primary key column of the record
//        lsSQL = MiscUtil.addCondition(lsSQL, " sEngnPtrn = " + SQLUtil.toSQL(fsCondition));
//
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//
//        try {
//            if (loRS.next()) {
//                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
//                    setValue(lnCtr, loRS.getObject(lnCtr));
//                }
//
//                pnEditMode = EditMode.UPDATE;
//
//                poJSON.put("result", "success");
//                poJSON.put("message", "Record loaded successfully.");
//            } else {
//                poJSON.put("result", "error");
//                poJSON.put("message", "No record to load.");
//            }
//        } catch (SQLException e) {
//            poJSON.put("result", "error");
//            poJSON.put("message", e.getMessage());
//        }
//
//        return poJSON;
    }
    
    public JSONObject openRecord(String fsValue, Integer fnEntryNo) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sModelIDx = " + SQLUtil.toSQL(fsValue)
                                                + " AND a.nEntryNox = " + SQLUtil.toSQL(fnEntryNo));

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
        String lsExclude = "sModelDsc»sMakeIDxx»sMakeDesc";
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setEntryNo(Integer.valueOf(MiscUtil.getNextCode(getTable(), "nEntryNox", false, poGRider.getConnection(), "")));
                
                //setEntryNo(MiscUtil.getNextCode(getTable(), "nEntryNox", false, null, null));
                setEntryBy(poGRider.getUserID());
                setEntryDte(poGRider.getServerDate());
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
                Model_Vehicle_Model_Engine_Pattern loOldEntity = new Model_Vehicle_Model_Engine_Pattern(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getModelID(),this.getEntryNo());

                if ("success".equals((String) loJSON.get("result"))) {
                    setModified(poGRider.getUserID());
                    setModifiedDte(poGRider.getServerDate());
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, " sModelIDx = " + SQLUtil.toSQL(this.getModelID()) + " AND nEntryNox = " + SQLUtil.toSQL(this.getEntryNo()), lsExclude);

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
        return    "  SELECT "                                               
                + "  a.sModelIDx "                                          
                + ", a.nEntryNox "                                          
                + ", a.sEngnPtrn "                                          
                + ", a.nEngnLenx "                                          
                + ", a.sEntryByx "                                          
                + ", a.dEntryDte "                                          
                + ", a.sModified "                                          
                + ", a.dModified "                                          
                + ", b.sModelDsc "                                          
                + ", c.sMakeIDxx "                                          
                + ", c.sMakeDesc "                                          
                + "FROM vehicle_model_engine_pattern a "                    
                + "LEFT JOIN vehicle_model b ON b.sModelIDx = a.sModelIDx " 
                + "LEFT JOIN vehicle_make c ON c.sMakeIDxx = b.sMakeIDxx  " ;
        
//        " SELECT " +
//                "   a.nEntryNox " +
//                " , IFNULL(a.sEngnPtrn, '') sEngnPtrn " +
//                " , a.nEngnLenx  " +
//                " , a.sEntryByx " +
//                " , a.dEntryDte " +
//                " , IFNULL(c.sMakeIDxx, '') sMakeIDxx " +
//                " , IFNULL(c.sMakeDesc, '') sMakeDesc " +
//                " , IFNULL(a.sModelIDx, '') sModelIDx " +
//                " , IFNULL(b.sModelDsc, '') sModelDsc " +
//                " , '2' as nCodeType " +
//                " , 'ENGINE' as sCodeType " +
//                " , a.sModified " +
//                " , a.dModified " +
//                " FROM vehicle_model_engine_pattern a" +
//                " LEFT JOIN vehicle_model b ON b.sModelIDx = a.sModelIDx" +
//                " LEFT JOIN vehicle_make c ON c.sMakeIDxx = b.sMakeIDxx" ;
        
    }
    
     /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelID(String fsValue) {
        return setValue("sModelIDx", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getModelID() {
        return (String) getValue("sModelIDx");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEngnPtrn(String fsValue) {
        return setValue("sEngnPtrn", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getEngnPtrn() {
        return (String) getValue("sEngnPtrn");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMakeID(String fsValue) {
        return setValue("sMakeIDxx", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getMakeID() {
        return (String) getValue("sMakeIDxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setMakeDesc(String fsValue) {
        return setValue("sMakeDesc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getMakeDesc() {
        return (String) getValue("sMakeDesc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelDsc(String fsValue) {
        return setValue("sModelDsc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getModelDsc() {
        return (String) getValue("sModelDsc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fnValue
     * @return result as success/failed
     */
    public JSONObject setEntryNo(Integer fnValue) {
        return setValue("nEntryNox", fnValue);
    }

    /**
     * @return The Value of this record.
     */
    public Integer getEntryNo() {
        return Integer.parseInt(String.valueOf( getValue("nEntryNox")));
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fnValue
     * @return result as success/failed
     */
    public JSONObject setEngnLen(Integer fnValue) {
        return setValue("nEngnLenx", fnValue);
    }

    /**
     * @return The Value of this record.
     */
    public Integer getEngnLen() {
        return Integer.parseInt(String.valueOf( getValue("nEngnLenx")));
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEntryBy(String fsValue) {
        return setValue("sEntryByx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEntryBy() {
        return (String) getValue("sEntryByx");
    }
    
    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setEntryDte(Date fdValue) {
        return setValue("dEntryDte", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getEntryDte() {
        return (Date) getValue("dEntryDte");
    }
    
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
    
    
}

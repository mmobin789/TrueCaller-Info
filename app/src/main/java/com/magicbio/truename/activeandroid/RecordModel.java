package com.magicbio.truename.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Ahmed Bilal on 1/7/2019.
 */
@Table(name = "Recordings")
public class RecordModel extends Model {
    @Column(name = "Path")
    public String path;

    @Column(name = "Rid")
    public String Rid;

    public static List<RecordModel> getAll() {
        return new Select()
                .all()
                .from(RecordModel.class)
                .execute();
    }

    public static RecordModel getRandom(String id) {
        if (id == null) {
            id = "";
        }
        return new Select()
                .from(RecordModel.class)
                .where("Rid = ?", id)
                .executeSingle();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRid() {
        return Rid;
    }

    public void setRid(String id) {
        this.Rid = id;
    }
}

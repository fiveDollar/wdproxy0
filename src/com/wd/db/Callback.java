package com.wd.db;
import java.sql.ResultSet;

public abstract class Callback{
	public abstract Object[] CallbackHandler(ResultSet rs);
}
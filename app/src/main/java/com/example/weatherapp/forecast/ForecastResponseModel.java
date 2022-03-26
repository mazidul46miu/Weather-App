package com.example.weatherapp.forecast;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ForecastResponseModel{

	@SerializedName("city")
	private City city;

	@SerializedName("cnt")
	private int cnt;

	@SerializedName("cod")
	private String cod;

	@SerializedName("message")
	private int message;

	@SerializedName("list")
	private List<ListItem> list;

	public City getCity(){
		return city;
	}

	public int getCnt(){
		return cnt;
	}

	public String getCod(){
		return cod;
	}

	public int getMessage(){
		return message;
	}

	public List<ListItem> getList(){
		return list;
	}

	@Override
	public String toString() {
		return "ForecastResponseModel{" +
				"city=" + city +
				", cnt=" + cnt +
				", cod='" + cod + '\'' +
				", message=" + message +
				", list=" + list +
				'}';
	}
}
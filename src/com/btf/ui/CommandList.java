package com.btf.ui;

public interface CommandList {
	static final String EVENT_ABOUT = "event_about";
	static final String EVENT_PROBLEM = "event_problem";
	
	static final String EVENT_OPEN = "event_open";
	static final String EVENT_EXIT = "event_exit";
	static final String EVENT_SAVE = "event_save";
	static final String EVENT_OPEN_PHOTO_BROWSER = "event_open_photo_browser";
	
	static final String EVENT_PB_REMOVE = "event_pb_remove";
	static final String EVENT_PB_PREVIOUS = "event_pb_previous";
	static final String EVENT_PB_NEXT = "event_pb_next";
	static final String EVENT_PB_VIEW = "event_pb_view";
	static final String EVENT_PB_RANDOM = "event_pb_random";
	static final String EVENT_PB_SEARCH = "event_pb_search";
	static final String EVENT_PB_CANCEL = "event_pb_cancel";
	static final String EVENT_PB_FINAL = "event_pb_final";
	
	static final String EVENT_HalfToning = "event_halftoning";
	static final String EVENT_HalfToning_GenerateWedge = "event_halftoning_gw";
	static final String EVENT_PROJECTION_CYLINDER = "event_projection_cylinder";
	static final String EVENT_PROJECTION_CYLINDER_2 = "event_projection_cylinder_2";
	static final String EVENT_IMAGE_MOSAIC = "event_image_mosaic";
	static final String EVENT_NOISE_GAUSSIAN = "event_noise_gaussian";
	static final String EVENT_NOISE_SAP = "event_noise_salt_and_pepper";
	
	static final String EVENT_TOGRAY_1 = "event_togray_1";
	static final String EVENT_TOGRAY_2 = "event_togray_2";
	static final String EVENT_TOGRAY_3 = "event_togray_3";
	static final String EVENT_TOGRAY_4 = "event_togray_4";
	static final String EVENT_TOGRAY_5 = "event_togray_5";
	static final String EVENT_TOGRAY_6 = "event_togray_6";
	static final String EVENT_TOGRAY_7 = "event_togray_7";
	static final String EVENT_TOGRAY_8 = "event_togray_8";
	static final String EVENT_SHOWBLUE = "event_showblue";
	static final String EVENT_SHOWGREEN = "event_showgreen";
	static final String EVENT_SHOWRED = "event_showred";
	static final String EVENT_PSEUDO_COLOR_YELLOW = "event_pseudo_color_yellow";
	
	static final String EVENT_SPATIAL_FILTER = "event_spatial_filter";
	static final String EVENT_SPATIAL_LAPLACIAN = "event_spatial_laplacian";
	static final String EVENT_SPATIAL_AVERAGE = "event_spatial_average";
	static final String EVENT_SPATIAL_MEDIAN = "event_spatial_median";
	
	static final String EVENT_GRAPHIC_HISTOGRAM = "event_graphic_histogram";
	static final String EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION = "event_graphic_histogram_equalization";
	static final String EVENT_GRAPHIC_HISTOGRAM_EQUALIZATION_AVERAGE = "event_graphic_histogram_equalization_average";
	 
	static final String EVENT_ZOOMOUT_2 = "event_zoomout_2";
}

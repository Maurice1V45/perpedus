package com.perpedus.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.perpedus.android.PerpedusApplication;
import com.perpedus.android.R;
import com.perpedus.android.comparator.PlaceTypesComparator;
import com.perpedus.android.listener.RetrievePlaceDetailsListener;
import com.perpedus.android.listener.RetrievePlacesListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class that holds provides data about places
 */
public class PlacesHelper {

    /**
     * Constructor
     */
    private PlacesHelper() {

        // private constructor
        context = PerpedusApplication.getInstance().getApplicationContext();
        initBitmapOptions();
        initBitmaps();
        initPlaceTypesMap();
    }

    /**
     * Bitmap Options initializer
     */
    private void initBitmapOptions() {
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;
    }


    private BitmapFactory.Options bitmapOptions;
    private Context context;

    private static PlacesHelper instance = new PlacesHelper();

    private Bitmap blackMarkerBitmap;
    private Bitmap yellowMarkerBitmap;

    private Bitmap arrowBitmap1;
    private Bitmap arrowBitmap2;
    private Bitmap arrowBitmap3;
    private Bitmap arrowBitmap4;
    private Bitmap arrowBitmap5;
    private Bitmap arrowBitmap6;
    private Bitmap arrowBitmap7;
    private Bitmap arrowBitmap8;

    private Bitmap bitmapAccounting;
    private Bitmap bitmapAirport;
    private Bitmap bitmapAmusementPark;
    private Bitmap bitmapAquarium;
    private Bitmap bitmapArtGalery;
    private Bitmap bitmapAtm;
    private Bitmap bitmapBakery;
    private Bitmap bitmapBank;
    private Bitmap bitmapBar;
    private Bitmap bitmapBeautySalon;
    private Bitmap bitmapBicycleStore;
    private Bitmap bitmapBookStore;
    private Bitmap bitmapBowlingAlley;
    private Bitmap bitmapBusStation;
    private Bitmap bitmapCafe;
    private Bitmap bitmapCampground;
    private Bitmap bitmapCarDealer;
    private Bitmap bitmapCarRental;
    private Bitmap bitmapCarRepair;
    private Bitmap bitmapCarWash;
    private Bitmap bitmapCasino;
    private Bitmap bitmapCemetery;
    private Bitmap bitmapChurch;
    private Bitmap bitmapCityHall;
    private Bitmap bitmapClothingStore;
    private Bitmap bitmapConvenienceStore;
    private Bitmap bitmapCourthouse;
    private Bitmap bitmapDentist;
    private Bitmap bitmapDepartmentStore;
    private Bitmap bitmapDoctor;
    private Bitmap bitmapElectrician;
    private Bitmap bitmapElectronicsStore;
    private Bitmap bitmapEmbassy;
    private Bitmap bitmapEstablishment;
    private Bitmap bitmapFinance;
    private Bitmap bitmapFireStation;
    private Bitmap bitmapFlorist;
    private Bitmap bitmapFood;
    private Bitmap bitmapFuneralHome;
    private Bitmap bitmapFurnitureStore;
    private Bitmap bitmapGasStation;
    private Bitmap bitmapGeneralContractor;
    private Bitmap bitmapGroceryOrSupermarket;
    private Bitmap bitmapGym;
    private Bitmap bitmapHairCare;
    private Bitmap bitmapHardwareStore;
    private Bitmap bitmapHealth;
    private Bitmap bitmapHinduTemple;
    private Bitmap bitmapHomeGoodsStore;
    private Bitmap bitmapHospital;
    private Bitmap bitmapInsuranceAgency;
    private Bitmap bitmapJewelryStore;
    private Bitmap bitmapLaundry;
    private Bitmap bitmapLawyer;
    private Bitmap bitmapLibrary;
    private Bitmap bitmapLiquorStore;
    private Bitmap bitmapLocalGovernmentOffice;
    private Bitmap bitmapLocksmith;
    private Bitmap bitmapLodging;
    private Bitmap bitmapMealDelivery;
    private Bitmap bitmapMealTakeaway;
    private Bitmap bitmapMosque;
    private Bitmap bitmapMovieRental;
    private Bitmap bitmapMovieTheater;
    private Bitmap bitmapMovingCompany;
    private Bitmap bitmapMuseum;
    private Bitmap bitmapNightClub;
    private Bitmap bitmapPainter;
    private Bitmap bitmapPark;
    private Bitmap bitmapParking;
    private Bitmap bitmapPetStore;
    private Bitmap bitmapPharmacy;
    private Bitmap bitmapPhysiotherapist;
    private Bitmap bitmapPlaceOfWorkship;
    private Bitmap bitmapPlumber;
    private Bitmap bitmapPolice;
    private Bitmap bitmapPostOffice;
    private Bitmap bitmapRealEstateAgency;
    private Bitmap bitmapRestaurant;
    private Bitmap bitmapRoofingContractor;
    private Bitmap bitmapRvPark;
    private Bitmap bitmapSchool;
    private Bitmap bitmapShoeStore;
    private Bitmap bitmapShoppingMall;
    private Bitmap bitmapSpa;
    private Bitmap bitmapStadium;
    private Bitmap bitmapStorage;
    private Bitmap bitmapStore;
    private Bitmap bitmapSubwayStation;
    private Bitmap bitmapSynagogue;
    private Bitmap bitmapTaxiStand;
    private Bitmap bitmapTrainStation;
    private Bitmap bitmapTravelAgency;
    private Bitmap bitmapUniversity;
    private Bitmap bitmapVeterinaryCare;
    private Bitmap bitmapZoo;
    private Bitmap bitmapPointOfInterest;

    /**
     * 0 is string reference
     * 1 is bitmap reference
     * 2 is image reference
     * 3 is searchable flag (non-searchable will not appear in place type dialog)
     */
    private Map<String, Object[]> placeTypeMap = new HashMap<String, Object[]>();

    /**
     * Bitmaps initializer. We load them here so we can reuse them later
     */
    private void initBitmaps() {

        // place marker bitmaps (yellow & black)
        blackMarkerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_marker_black, bitmapOptions);
        yellowMarkerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_marker_red, bitmapOptions);

        // arrows bitmaps (used for out of bounds)
        arrowBitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_1, bitmapOptions);
        arrowBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_2, bitmapOptions);
        arrowBitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_3, bitmapOptions);
        arrowBitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_4, bitmapOptions);
        arrowBitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_5, bitmapOptions);
        arrowBitmap6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_6, bitmapOptions);
        arrowBitmap7 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_7, bitmapOptions);
        arrowBitmap8 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_arrow_8, bitmapOptions);

        // place type bitmaps for normal state
        bitmapAccounting = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_accounting, bitmapOptions);
        bitmapAirport = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_airport, bitmapOptions);
        bitmapAmusementPark = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_amusement_park, bitmapOptions);
        bitmapAquarium = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_aquarium, bitmapOptions);
        bitmapArtGalery = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_art_gallery, bitmapOptions);
        bitmapAtm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_atm, bitmapOptions);
        bitmapBakery = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bakery, bitmapOptions);
        bitmapBank = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bank, bitmapOptions);
        bitmapBar = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bar, bitmapOptions);
        bitmapBeautySalon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_beauty_salon, bitmapOptions);
        bitmapBicycleStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bicycle_store, bitmapOptions);
        bitmapBookStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_book_store, bitmapOptions);
        bitmapBowlingAlley = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bowling_alley, bitmapOptions);
        bitmapBusStation = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bus_station, bitmapOptions);
        bitmapCafe = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_cafe, bitmapOptions);
        bitmapCampground = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_campground, bitmapOptions);
        bitmapCarDealer = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car_dealer, bitmapOptions);
        bitmapCarRental = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car_rental, bitmapOptions);
        bitmapCarRepair = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car_repair, bitmapOptions);
        bitmapCarWash = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_car_wash, bitmapOptions);
        bitmapCasino = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_casino, bitmapOptions);
        bitmapCemetery = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_cemetery, bitmapOptions);
        bitmapChurch = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_church, bitmapOptions);
        bitmapCityHall = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_city_hall, bitmapOptions);
        bitmapClothingStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_clothing_store, bitmapOptions);
        bitmapConvenienceStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_convenience_store, bitmapOptions);
        bitmapCourthouse = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_courthouse, bitmapOptions);
        bitmapDentist = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_dentist, bitmapOptions);
        bitmapDepartmentStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_department_store, bitmapOptions);
        bitmapDoctor = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_doctor, bitmapOptions);
        bitmapElectrician = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_electrician, bitmapOptions);
        bitmapElectronicsStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_electronics_store, bitmapOptions);
        bitmapEmbassy = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_embassy, bitmapOptions);
        bitmapEstablishment = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_establishment, bitmapOptions);
        bitmapFinance = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_finance, bitmapOptions);
        bitmapFireStation = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_fire_station, bitmapOptions);
        bitmapFlorist = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_florist, bitmapOptions);
        bitmapFood = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_food, bitmapOptions);
        bitmapFuneralHome = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_funeral_home, bitmapOptions);
        bitmapFurnitureStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_furniture_store, bitmapOptions);
        bitmapGasStation = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_gas_station, bitmapOptions);
        bitmapGeneralContractor = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_general_contractor, bitmapOptions);
        bitmapGroceryOrSupermarket = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_grocery_or_supermarket, bitmapOptions);
        bitmapGym = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_gym, bitmapOptions);
        bitmapHairCare = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hair_care, bitmapOptions);
        bitmapHardwareStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hardware_store, bitmapOptions);
        bitmapHealth = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_health, bitmapOptions);
        bitmapHinduTemple = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hindu_temple, bitmapOptions);
        bitmapHomeGoodsStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_home_goods_store, bitmapOptions);
        bitmapHospital = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_hospital, bitmapOptions);
        bitmapInsuranceAgency = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_insurance_agency, bitmapOptions);
        bitmapJewelryStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_jewelry_store, bitmapOptions);
        bitmapLaundry = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_laundry, bitmapOptions);
        bitmapLawyer = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_lawyer, bitmapOptions);
        bitmapLibrary = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_library, bitmapOptions);
        bitmapLiquorStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_liquor_store, bitmapOptions);
        bitmapLocalGovernmentOffice = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_local_government_office, bitmapOptions);
        bitmapLocksmith = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_locksmith, bitmapOptions);
        bitmapLodging = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_lodging, bitmapOptions);
        bitmapMealDelivery = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_meal_delivery, bitmapOptions);
        bitmapMealTakeaway = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_meal_takeaway, bitmapOptions);
        bitmapMosque = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_mosque, bitmapOptions);
        bitmapMovieRental = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_movie_rental, bitmapOptions);
        bitmapMovieTheater = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_movie_theater, bitmapOptions);
        bitmapMovingCompany = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_moving_company, bitmapOptions);
        bitmapMuseum = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_museum, bitmapOptions);
        bitmapNightClub = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_night_club, bitmapOptions);
        bitmapPainter = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_painter, bitmapOptions);
        bitmapPark = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_park, bitmapOptions);
        bitmapParking = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_parking, bitmapOptions);
        bitmapPetStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_pet_store, bitmapOptions);
        bitmapPharmacy = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_pharmacy, bitmapOptions);
        bitmapPhysiotherapist = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_physiotherapist, bitmapOptions);
        bitmapPlaceOfWorkship = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_place_of_worship, bitmapOptions);
        bitmapPlumber = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_plumber, bitmapOptions);
        bitmapPolice = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_police, bitmapOptions);
        bitmapPostOffice = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_post_office, bitmapOptions);
        bitmapRealEstateAgency = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_real_estate_agency, bitmapOptions);
        bitmapRestaurant = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_restaurant, bitmapOptions);
        bitmapRoofingContractor = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_roofing_contractor, bitmapOptions);
        bitmapRvPark = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_rv_park, bitmapOptions);
        bitmapSchool = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_school, bitmapOptions);
        bitmapShoeStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_shoe_store, bitmapOptions);
        bitmapShoppingMall = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_shopping_mall, bitmapOptions);
        bitmapSpa = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_spa, bitmapOptions);
        bitmapStadium = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_stadium, bitmapOptions);
        bitmapStorage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_storage, bitmapOptions);
        bitmapStore = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_store, bitmapOptions);
        bitmapSubwayStation = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_subway_station, bitmapOptions);
        bitmapSynagogue = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_synagogue, bitmapOptions);
        bitmapTaxiStand = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_taxi_stand, bitmapOptions);
        bitmapTrainStation = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_train_station, bitmapOptions);
        bitmapTravelAgency = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_travel_agency, bitmapOptions);
        bitmapUniversity = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_university, bitmapOptions);
        bitmapVeterinaryCare = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_veterinary_care, bitmapOptions);
        bitmapZoo = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_zoo, bitmapOptions);
        bitmapPointOfInterest = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_point_of_interest, bitmapOptions);

    }

    /**
     * Initializes a hashmap that holds data about each place type
     */
    private void initPlaceTypesMap() {
        placeTypeMap.put("accounting", new Object[]{R.string.place_type_accounting, bitmapAccounting, R.drawable.icon_accounting, true});
        placeTypeMap.put("airport", new Object[]{R.string.place_type_airport, bitmapAirport, R.drawable.icon_airport, true});
        placeTypeMap.put("amusement_park", new Object[]{R.string.place_type_amusement_park, bitmapAmusementPark, R.drawable.icon_amusement_park, true});
        placeTypeMap.put("aquarium", new Object[]{R.string.place_type_aquarium, bitmapAquarium, R.drawable.icon_aquarium, true});
        placeTypeMap.put("art_gallery", new Object[]{R.string.place_type_art_gallery, bitmapArtGalery, R.drawable.icon_art_gallery, true});
        placeTypeMap.put("atm", new Object[]{R.string.place_type_atm, bitmapAtm, R.drawable.icon_atm, true});
        placeTypeMap.put("bakery", new Object[]{R.string.place_type_bakery, bitmapBakery, R.drawable.icon_bakery, true});
        placeTypeMap.put("bank", new Object[]{R.string.place_type_bank, bitmapBank, R.drawable.icon_bank, true});
        placeTypeMap.put("bar", new Object[]{R.string.place_type_bar, bitmapBar, R.drawable.icon_bar, true});
        placeTypeMap.put("beauty_salon", new Object[]{R.string.place_type_beauty_salon, bitmapBeautySalon, R.drawable.icon_beauty_salon, true});
        placeTypeMap.put("bicycle_store", new Object[]{R.string.place_type_bicycle_store, bitmapBicycleStore, R.drawable.icon_bicycle_store, true});
        placeTypeMap.put("book_store", new Object[]{R.string.place_type_book_store, bitmapBookStore, R.drawable.icon_book_store, true});
        placeTypeMap.put("bowling_alley", new Object[]{R.string.place_type_bowling_alley, bitmapBowlingAlley, R.drawable.icon_bowling_alley, true});
        placeTypeMap.put("bus_station", new Object[]{R.string.place_type_bus_station, bitmapBusStation, R.drawable.icon_bus_station, true});
        placeTypeMap.put("cafe", new Object[]{R.string.place_type_cafe, bitmapCafe, R.drawable.icon_cafe, true});
        placeTypeMap.put("campground", new Object[]{R.string.place_type_campground, bitmapCampground, R.drawable.icon_campground, true});
        placeTypeMap.put("car_dealer", new Object[]{R.string.place_type_car_dealer, bitmapCarDealer, R.drawable.icon_car_dealer, true});
        placeTypeMap.put("car_rental", new Object[]{R.string.place_type_car_rental, bitmapCarRental, R.drawable.icon_car_rental, true});
        placeTypeMap.put("car_repair", new Object[]{R.string.place_type_car_repair, bitmapCarRepair, R.drawable.icon_car_repair, true});
        placeTypeMap.put("car_wash", new Object[]{R.string.place_type_car_wash, bitmapCarWash, R.drawable.icon_car_wash, true});
        placeTypeMap.put("casino", new Object[]{R.string.place_type_casino, bitmapCasino, R.drawable.icon_casino, true});
        placeTypeMap.put("cemetery", new Object[]{R.string.place_type_cemetery, bitmapCemetery, R.drawable.icon_cemetery, true});
        placeTypeMap.put("church", new Object[]{R.string.place_type_church, bitmapChurch, R.drawable.icon_church, true});
        placeTypeMap.put("city_hall", new Object[]{R.string.place_type_city_hall, bitmapCityHall, R.drawable.icon_city_hall, true});
        placeTypeMap.put("clothing_store", new Object[]{R.string.place_type_clothing_store, bitmapClothingStore, R.drawable.icon_clothing_store, true});
        placeTypeMap.put("convenience_store", new Object[]{R.string.place_type_convenience_store, bitmapConvenienceStore, R.drawable.icon_convenience_store, true});
        placeTypeMap.put("courthouse", new Object[]{R.string.place_type_courthouse, bitmapCourthouse, R.drawable.icon_courthouse, true});
        placeTypeMap.put("dentist", new Object[]{R.string.place_type_dentist, bitmapDentist, R.drawable.icon_dentist, true});
        placeTypeMap.put("department_store", new Object[]{R.string.place_type_department_store, bitmapDepartmentStore, R.drawable.icon_department_store, true});
        placeTypeMap.put("doctor", new Object[]{R.string.place_type_doctor, bitmapDoctor, R.drawable.icon_doctor, true});
        placeTypeMap.put("electrician", new Object[]{R.string.place_type_electrician, bitmapElectrician, R.drawable.icon_electrician, true});
        placeTypeMap.put("electronics_store", new Object[]{R.string.place_type_electronics_store, bitmapElectronicsStore, R.drawable.icon_electronics_store, true});
        placeTypeMap.put("embassy", new Object[]{R.string.place_type_embassy, bitmapEmbassy, R.drawable.icon_embassy, true});
        placeTypeMap.put("establishment", new Object[]{R.string.place_type_establishment, bitmapEstablishment, R.drawable.icon_establishment, true});
        placeTypeMap.put("finance", new Object[]{R.string.place_type_finance, bitmapFinance, R.drawable.icon_finance, true});
        placeTypeMap.put("fire_station", new Object[]{R.string.place_type_fire_station, bitmapFireStation, R.drawable.icon_fire_station, true});
        placeTypeMap.put("florist", new Object[]{R.string.place_type_florist, bitmapFlorist, R.drawable.icon_florist, true});
        placeTypeMap.put("food", new Object[]{R.string.place_type_food, bitmapFood, R.drawable.icon_food, true});
        placeTypeMap.put("funeral_home", new Object[]{R.string.place_type_funeral_home, bitmapFuneralHome, R.drawable.icon_funeral_home, true});
        placeTypeMap.put("furniture_store", new Object[]{R.string.place_type_furniture_store, bitmapFurnitureStore, R.drawable.icon_furniture_store, true});
        placeTypeMap.put("gas_station", new Object[]{R.string.place_type_gas_station, bitmapGasStation, R.drawable.icon_gas_station, true});
        placeTypeMap.put("general_contractor", new Object[]{R.string.place_type_general_contractor, bitmapGeneralContractor, R.drawable.icon_general_contractor, true});
        placeTypeMap.put("grocery_or_supermarket", new Object[]{R.string.place_type_grocery_or_supermarket, bitmapGroceryOrSupermarket, R.drawable.icon_grocery_or_supermarket, true});
        placeTypeMap.put("gym", new Object[]{R.string.place_type_gym, bitmapGym, R.drawable.icon_gym, true});
        placeTypeMap.put("hair_care", new Object[]{R.string.place_type_hair_care, bitmapHairCare, R.drawable.icon_hair_care, true});
        placeTypeMap.put("hardware_store", new Object[]{R.string.place_type_hardware_store, bitmapHardwareStore, R.drawable.icon_hardware_store, true});
        placeTypeMap.put("health", new Object[]{R.string.place_type_health, bitmapHealth, R.drawable.icon_health, true});
        placeTypeMap.put("hindu_temple", new Object[]{R.string.place_type_hindu_temple, bitmapHinduTemple, R.drawable.icon_hindu_temple, true});
        placeTypeMap.put("home_goods_store", new Object[]{R.string.place_type_home_goods_store, bitmapHomeGoodsStore, R.drawable.icon_home_goods_store, true});
        placeTypeMap.put("hospital", new Object[]{R.string.place_type_hospital, bitmapHospital, R.drawable.icon_hospital, true});
        placeTypeMap.put("insurance_agency", new Object[]{R.string.place_type_insurance_agency, bitmapInsuranceAgency, R.drawable.icon_insurance_agency, true});
        placeTypeMap.put("jewelry_store", new Object[]{R.string.place_type_jewelry_store, bitmapJewelryStore, R.drawable.icon_jewelry_store, true});
        placeTypeMap.put("laundry", new Object[]{R.string.place_type_laundry, bitmapLaundry, R.drawable.icon_laundry, true});
        placeTypeMap.put("lawyer", new Object[]{R.string.place_type_lawyer, bitmapLawyer, R.drawable.icon_lawyer, true});
        placeTypeMap.put("library", new Object[]{R.string.place_type_library, bitmapLibrary, R.drawable.icon_library, true});
        placeTypeMap.put("liquor_store", new Object[]{R.string.place_type_liquor_store, bitmapLiquorStore, R.drawable.icon_liquor_store, true});
        placeTypeMap.put("local_government_office", new Object[]{R.string.place_type_local_government_office, bitmapLocalGovernmentOffice, R.drawable.icon_local_government_office, true});
        placeTypeMap.put("locksmith", new Object[]{R.string.place_type_locksmith, bitmapLocksmith, R.drawable.icon_locksmith, true});
        placeTypeMap.put("lodging", new Object[]{R.string.place_type_lodging, bitmapLodging, R.drawable.icon_lodging, true});
        placeTypeMap.put("meal_delivery", new Object[]{R.string.place_type_meal_delivery, bitmapMealDelivery, R.drawable.icon_meal_delivery, true});
        placeTypeMap.put("meal_takeaway", new Object[]{R.string.place_type_meal_takeaway, bitmapMealTakeaway, R.drawable.icon_meal_takeaway, true});
        placeTypeMap.put("mosque", new Object[]{R.string.place_type_mosque, bitmapMosque, R.drawable.icon_mosque, true});
        placeTypeMap.put("movie_rental", new Object[]{R.string.place_type_movie_rental, bitmapMovieRental, R.drawable.icon_movie_rental, true});
        placeTypeMap.put("movie_theater", new Object[]{R.string.place_type_movie_theater, bitmapMovieTheater, R.drawable.icon_movie_theater, true});
        placeTypeMap.put("moving_company", new Object[]{R.string.place_type_moving_company, bitmapMovingCompany, R.drawable.icon_moving_company, true});
        placeTypeMap.put("museum", new Object[]{R.string.place_type_museum, bitmapMuseum, R.drawable.icon_museum, true});
        placeTypeMap.put("night_club", new Object[]{R.string.place_type_night_club, bitmapNightClub, R.drawable.icon_night_club, true});
        placeTypeMap.put("painter", new Object[]{R.string.place_type_painter, bitmapPainter, R.drawable.icon_painter, true});
        placeTypeMap.put("park", new Object[]{R.string.place_type_park, bitmapPark, R.drawable.icon_park, true});
        placeTypeMap.put("parking", new Object[]{R.string.place_type_parking, bitmapParking, R.drawable.icon_parking, true});
        placeTypeMap.put("pet_store", new Object[]{R.string.place_type_pet_store, bitmapPetStore, R.drawable.icon_pet_store, true});
        placeTypeMap.put("pharmacy", new Object[]{R.string.place_type_pharmacy, bitmapPharmacy, R.drawable.icon_pharmacy, true});
        placeTypeMap.put("physiotherapist", new Object[]{R.string.place_type_physiotherapist, bitmapPhysiotherapist, R.drawable.icon_physiotherapist, true});
        placeTypeMap.put("place_of_worship", new Object[]{R.string.place_type_place_of_worship, bitmapPlaceOfWorkship, R.drawable.icon_place_of_worship, true});
        placeTypeMap.put("plumber", new Object[]{R.string.place_type_plumber, bitmapPlumber, R.drawable.icon_plumber, true});
        placeTypeMap.put("police", new Object[]{R.string.place_type_police, bitmapPolice, R.drawable.icon_police, true});
        placeTypeMap.put("post_office", new Object[]{R.string.place_type_post_office, bitmapPostOffice, R.drawable.icon_post_office, true});
        placeTypeMap.put("real_estate_agency", new Object[]{R.string.place_type_real_estate_agency, bitmapRealEstateAgency, R.drawable.icon_real_estate_agency, true});
        placeTypeMap.put("restaurant", new Object[]{R.string.place_type_restaurant, bitmapRestaurant, R.drawable.icon_restaurant, true});
        placeTypeMap.put("roofing_contractor", new Object[]{R.string.place_type_roofing_contractor, bitmapRoofingContractor, R.drawable.icon_roofing_contractor, true});
        placeTypeMap.put("rv_park", new Object[]{R.string.place_type_rv_park, bitmapRvPark, R.drawable.icon_rv_park, true});
        placeTypeMap.put("school", new Object[]{R.string.place_type_school, bitmapSchool, R.drawable.icon_school, true});
        placeTypeMap.put("shoe_store", new Object[]{R.string.place_type_shoe_store, bitmapShoeStore, R.drawable.icon_shoe_store, true});
        placeTypeMap.put("shopping_mall", new Object[]{R.string.place_type_shopping_mall, bitmapShoppingMall, R.drawable.icon_shopping_mall, true});
        placeTypeMap.put("spa", new Object[]{R.string.place_type_spa, bitmapSpa, R.drawable.icon_spa, true});
        placeTypeMap.put("stadium", new Object[]{R.string.place_type_stadium, bitmapStadium, R.drawable.icon_stadium, true});
        placeTypeMap.put("storage", new Object[]{R.string.place_type_storage, bitmapStorage, R.drawable.icon_storage, true});
        placeTypeMap.put("store", new Object[]{R.string.place_type_store, bitmapStore, R.drawable.icon_store, true});
        placeTypeMap.put("subway_station", new Object[]{R.string.place_type_subway_station, bitmapSubwayStation, R.drawable.icon_subway_station, true});
        placeTypeMap.put("synagogue", new Object[]{R.string.place_type_synagogue, bitmapSynagogue, R.drawable.icon_synagogue, true});
        placeTypeMap.put("taxi_stand", new Object[]{R.string.place_type_taxi_stand, bitmapTaxiStand, R.drawable.icon_taxi_stand, true});
        placeTypeMap.put("train_station", new Object[]{R.string.place_type_train_station, bitmapTrainStation, R.drawable.icon_train_station, true});
        placeTypeMap.put("travel_agency", new Object[]{R.string.place_type_travel_agency, bitmapTravelAgency, R.drawable.icon_travel_agency, true});
        placeTypeMap.put("university", new Object[]{R.string.place_type_university, bitmapUniversity, R.drawable.icon_university, true});
        placeTypeMap.put("veterinary_care", new Object[]{R.string.place_type_veterinary_care, bitmapVeterinaryCare, R.drawable.icon_veterinary_care, true});
        placeTypeMap.put("zoo", new Object[]{R.string.place_type_zoo, bitmapZoo, R.drawable.icon_zoo, true});

        placeTypeMap.put("administrative_area_level_1", new Object[]{R.string.place_type_administrative_area_level_1, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("administrative_area_level_2", new Object[]{R.string.place_type_administrative_area_level_2, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("administrative_area_level_3", new Object[]{R.string.place_type_administrative_area_level_3, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("administrative_area_level_4", new Object[]{R.string.place_type_administrative_area_level_4, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("administrative_area_level_5", new Object[]{R.string.place_type_administrative_area_level_5, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("colloquial_area", new Object[]{R.string.place_type_colloquial_area, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("country", new Object[]{R.string.place_type_country, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("floor", new Object[]{R.string.place_type_floor, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("geocode", new Object[]{R.string.place_type_geocode, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("intersection", new Object[]{R.string.place_type_intersection, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("locality", new Object[]{R.string.place_type_locality, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("natural_feature", new Object[]{R.string.place_type_natural_feature, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("neighborhood", new Object[]{R.string.place_type_neighborhood, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("political", new Object[]{R.string.place_type_political, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("point_of_interest", new Object[]{R.string.place_type_point_of_interest, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("post_box", new Object[]{R.string.place_type_post_box, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("postal_code", new Object[]{R.string.place_type_postal_code, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("postal_code_prefix", new Object[]{R.string.place_type_postal_code_prefix, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("postal_code_suffix", new Object[]{R.string.place_type_postal_code_suffix, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("postal_town", new Object[]{R.string.place_type_postal_town, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("premise", new Object[]{R.string.place_type_premise, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("room", new Object[]{R.string.place_type_room, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("route", new Object[]{R.string.place_type_route, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("street_address", new Object[]{R.string.place_type_street_address, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("street_number", new Object[]{R.string.place_type_street_number, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality", new Object[]{R.string.place_type_sublocality, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality_level_5", new Object[]{R.string.place_type_sublocality_level_5, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality_level_4", new Object[]{R.string.place_type_sublocality_level_4, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality_level_3", new Object[]{R.string.place_type_sublocality_level_3, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality_level_2", new Object[]{R.string.place_type_sublocality_level_2, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("sublocality_level_1", new Object[]{R.string.place_type_sublocality_level_1, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("subpremise", new Object[]{R.string.place_type_subpremise, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
        placeTypeMap.put("transit_station", new Object[]{R.string.place_type_train_station, bitmapPointOfInterest, R.drawable.icon_point_of_interest, false});
    }

    /**
     * Returns the instance of the singleton
     *
     * @return
     */
    public static PlacesHelper getInstance() {
        return instance;
    }

    /**
     * Returns the bitmap reference for the given place type id
     *
     * @param placeType
     * @return
     */
    public Bitmap getPlaceBitmap(String placeType) {
        Object[] placeProperties = placeTypeMap.get(placeType);
        if (placeProperties == null) {
            return  bitmapPointOfInterest;
        } else {
            return (Bitmap) placeProperties[1];
        }
    }

    /**
     * Returns the drawable reference for the given place type id
     *
     * @param placeType
     * @return
     */
    public int getPlaceIcon(String placeType) {
        Object[] placeProperties = placeTypeMap.get(placeType);
        if (placeProperties == null) {
            return 0;
        } else {
            return (Integer) placeProperties[2];
        }
    }

    /**
     * Returns the place type name for the given place type id
     *
     * @param placeType
     * @return
     */
    public String getPlaceTypeName(String placeType) {
        Object[] placeProperties = placeTypeMap.get(placeType);
        if (placeProperties == null) {
            return placeType;
            //return context.getString(R.string.place_type_unknown);
        } else {
            return context.getString((int) placeProperties[0]);
        }
    }

    /**
     * Returns a list of all searchable place types, sorted alphabetically
     *
     * @param context
     * @return
     */
    public List<String> getSearchablePlaceTypes(Context context) {

        // get all searchable place types
        List<Map.Entry<String, Object[]>> placeTypes = new ArrayList<Map.Entry<String, Object[]>>();
        for (Map.Entry<String, Object[]> entry : placeTypeMap.entrySet()) {
            boolean searchable = (boolean) entry.getValue()[3];
            if (searchable) {
                placeTypes.add(entry);
            }
        }

        // sort place types alphabetically
        Collections.sort(placeTypes, new PlaceTypesComparator(context));

        // get place types ids
        List<String> placeTypeIds = new ArrayList<String>();
        for (Map.Entry<String, Object[]> entry : placeTypes) {
            placeTypeIds.add(entry.getKey());
        }

        return placeTypeIds;
    }

    /**
     * Returns the red or black marker bitmap
     *
     * @param focused
     * @return
     */
    public Bitmap getMarkerBitmap(boolean focused) {
        return focused ? yellowMarkerBitmap : blackMarkerBitmap;
    }

    /**
     * Returns an arrow bitmap depending on the position of outOfBounds
     *
     * @param outOfBounds
     * @return
     */
    public Bitmap getArrowBitmap(int outOfBounds) {
        switch (outOfBounds) {
            case 1:
                return arrowBitmap1;
            case 2:
                return arrowBitmap2;
            case 3:
                return arrowBitmap3;
            case 4:
                return arrowBitmap4;
            case 5:
                return arrowBitmap5;
            case 6:
                return arrowBitmap6;
            case 7:
                return arrowBitmap7;
            case 8:
                return arrowBitmap8;
            default:
                return arrowBitmap1;
        }
    }

    public void retrievePlacesAsync(final RetrievePlacesListener listener, final String keyword, final String radius, final float latitude, final float longitude, final String language, final String type) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String response = UrlUtils.getJsonFromUrl(UrlUtils.buildPlacesLink(keyword, latitude, longitude, radius, language, type));
                    listener.onPlacesRetrieved(response);
                } catch (Exception e) {
                    listener.onPlacesRetrievedError();
                }
            }
        });

        thread.start();
    }

    public void retrievePlaceDetailsAsync(final RetrievePlaceDetailsListener listener, final String placeId, final String language) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String response = UrlUtils.getJsonFromUrl(UrlUtils.buildPlaceDetailsLink(placeId, language));
                    listener.onPlaceDetailsRetrieved(response);
                } catch (Exception e) {
                    listener.onPlaceDetailsRetrievedError();
                }
            }
        });

        thread.start();
    }
}

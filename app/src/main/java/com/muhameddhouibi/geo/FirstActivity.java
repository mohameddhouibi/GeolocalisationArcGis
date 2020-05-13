package com.muhameddhouibi.geo;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.material.snackbar.Snackbar;

public class FirstActivity extends AppCompatActivity {
    private MapView mMapView;
    private GraphicsOverlay mGraphicsOverlay;
    private Button btnStreet ;
    private Button btnImagerie ;
    private Button btnTopographie ;
    private Graphic coordinateLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mMapView = findViewById(R.id.mapView);

        btnStreet = findViewById(R.id.Street);
        btnImagerie = findViewById(R.id.IMAGERY);
        btnTopographie = findViewById(R.id.TOPOGRAPHIC);

        btnStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,FirstActivity.class);
                startActivity(i);
            }
        });
        btnImagerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,SecondActivity.class);
                startActivity(i);
            }
        });
        btnTopographie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FirstActivity.this,ThirdActivity.class);
                startActivity(i);
            }
        });
        setupMap();
        mMapView.setOnTouchListener(new ShowCoordinatesMapTouchListener(this, mMapView));
    }
    private void createGraphicsOverlay() {
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
    }
    private void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.STREETS;
            double latitude = 36.813102;
            double longitude = 10.129154;
            int levelOfDetail = 13;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            ArcGISRuntimeEnvironment.setLicense(getResources().getString(R.string.arcgis_license_key));
            mMapView.setMap(map);


            Point initialPoint = new Point(0,0, SpatialReferences.getWgs84());
            coordinateLocation = new Graphic(initialPoint,
            new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f));
            mMapView.getGraphicsOverlays().add(new GraphicsOverlay());
            mMapView.getGraphicsOverlays().get(0).getGraphics().add(coordinateLocation);
            toCoordinateNotationFromPoint(initialPoint);
        }
    }



    /**
     * A map touch listener that updates formatted coordinates when a user taps on a location in the associated MapView.
     */
    private class ShowCoordinatesMapTouchListener extends DefaultMapViewOnTouchListener {

        public ShowCoordinatesMapTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        /**
         * Overrides the onSingleTapConfirmed gesture on the MapView, showing formatted coordinates of the tapped location.
         * @param e the motion event
         * @return true if the listener has consumed the event; false otherwise
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // convert the screen location where user tapped into a map point
            Point tapPoint = mMapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
            toCoordinateNotationFromPoint(tapPoint);
            return true;
        }

    }
    private void toCoordinateNotationFromPoint(Point newLocation) {
        if ((newLocation != null) && (! newLocation.isEmpty())) {
            coordinateLocation.setGeometry(newLocation);

            try {
                // use CoordinateFormatter to convert to Latitude Longitude, formatted as Decimal Degrees
               String mLatLongDDValue = CoordinateFormatter.toLatitudeLongitude(newLocation,
                        CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES, 4);
                Toast toast=Toast. makeText(getApplicationContext(),mLatLongDDValue,Toast. LENGTH_SHORT);
                toast. show();

            }
            catch (ArcGISRuntimeException convertException) {
                String message = String.format("%s Point at '%s'\n%s", getString(R.string.failed_convert),
                        newLocation.toString(), convertException.getMessage());
                Snackbar.make(mMapView, message, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }
}


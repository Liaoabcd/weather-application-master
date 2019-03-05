package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private String cityname;
    private String weather[];
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            weather = new String[]{"Rai", "Rai", "Clo", "Win", "Cel"};
            String stringUrl = "http://api.openweathermap.org/data/2.5/forecast?q=Chongqing,cn&mode=json&APPID=cb2f824918be19e1920bb593313a20e3";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);
                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line, tem;
                flag = false;
                while ((line = reader.readLine()) != null) {
                    flag = true;
                    // Mainly needed for debugging
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");

                    String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)/ 3 * 3 );
                    if (Integer.valueOf(hour) < 10) {
                        hour = "0" + hour;
                    }
                    String mes = dateFormat.format(Calendar.getInstance().getTime()) + hour;
                    int index1 = line.indexOf("temp", line.indexOf(mes) - 320) + 6;
                    System.out.println(line.substring(index1, index1 + 6));
                    tem = String.valueOf((int) (Double.valueOf(line.substring(index1, index1 + 6)) - 273.15));
                    int index2 = line.indexOf("main", index1);
                    weather[0] = line.substring(index2 + 7, index2 + 10);
                    cityname = "Chongqing";
                    int day = cal.get(Calendar.DATE);
                    for (int i = 1; i <=4; ++i) {
                        cal.set(Calendar.DATE, day + i);
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
                        hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)/ 3 * 3 );
                        if (Integer.valueOf(hour) < 10) {
                            hour = "0" + hour;
                        }
                        mes = dateFormat.format(cal.getTime()) + hour;
                        index1 = line.indexOf(mes) - 303;
                        index2 = line.indexOf("main", index1);
                        weather[i] = line.substring(index2 + 7, index2 + 10);
                    }

                   // Log.d("TAG", line);
                   // buffer.append(line + "\n");
                    buffer.append(tem + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
           // ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);

            if (temperature != null) {
                ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
            }
            //Update the location displayed
            if (cityname != null) {
                ((TextView) findViewById(R.id.tv_location)).setText(cityname);
            }
            //Update the date displayed
            Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
            String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            if (Integer.parseInt(month) < 10) {
                month = "0" + month;
            }
            if (Integer.parseInt(day) < 10) {
                day = "0" + day;
            }
            ((TextView) findViewById(R.id.tv_date)).setText(day + '/' + month + '/' + year);
            //Update the week displayed
            String[] weekValue = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            ((TextView) findViewById(R.id.tv_day1)).setText(weekValue[week]);
            ((TextView) findViewById(R.id.tv_day2)).setText(weekValue[(week + 1) % 7].substring(0, 3));
            ((TextView) findViewById(R.id.tv_day3)).setText(weekValue[(week + 2) % 7].substring(0, 3));
            ((TextView) findViewById(R.id.tv_day4)).setText(weekValue[(week + 3) % 7].substring(0, 3));
            ((TextView) findViewById(R.id.tv_day5)).setText(weekValue[(week + 4) % 7].substring(0, 3));
            //Update the weather displayed
            setWeatherIcon(R.id.img_weather_condition1, weather[0]);
            setWeatherIcon(R.id.img_weather_condition2, weather[1]);
            setWeatherIcon(R.id.img_weather_condition3, weather[2]);
            setWeatherIcon(R.id.img_weather_condition4, weather[3]);
            setWeatherIcon(R.id.img_weather_condition5, weather[4]);

            if (flag) {
                Toast.makeText(getBaseContext(), "Updated Success.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getBaseContext(), "Unable to connect to the network, update failed.", Toast.LENGTH_LONG).show();

        }
        protected void setWeatherIcon(int index, String wea) {
            if (wea.equals("Cle"))
                ((ImageView) findViewById(index)).setImageResource(R.drawable.sunny_small);
            else if (wea.equals("Rai"))
                ((ImageView) findViewById(index)).setImageResource(R.drawable.rainy_small);
            else if (wea.equals("Clo"))
                ((ImageView) findViewById(index)).setImageResource(R.drawable.partly_sunny_small);
            else if (wea.equals("Win"))
                ((ImageView) findViewById(index)).setImageResource(R.drawable.windy_small);
        }
    }
}

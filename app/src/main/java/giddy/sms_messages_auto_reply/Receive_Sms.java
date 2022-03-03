package giddy.sms_messages_auto_reply;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Random;

public class Receive_Sms extends BroadcastReceiver {

    private final String SMS="android.provider.Telephony.SMS_RECEIVED";
  Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()==SMS)
        {
            ctx=context;
            Bundle bundle=intent.getExtras();
            Object []obj= (Object[]) bundle.get("pdus");

            SmsMessage[] smsMessages=new SmsMessage[obj.length];
            for(int i=0; i<obj.length; i++)
            {
                smsMessages[i]=SmsMessage.createFromPdu((byte[])obj[i]);
            }
            String message=smsMessages[0].getMessageBody();
            String phoneNo=smsMessages[0].getOriginatingAddress();
            message=message.toLowerCase().toString();

            Toast.makeText(context,"Message from : "+smsMessages[0].getOriginatingAddress()+" \n "+smsMessages[0].getMessageBody(),Toast.LENGTH_LONG).show();

            try {
                Boolean state=true;
                MainActivity mainActivity=new MainActivity();
                String s=ReadJson(context);
                String phoneNo2=mainActivity.phoneNo2;
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray(message);
                Random random=new Random();
                int rand=random.nextInt(jsonArray.length());
                if(rand==jsonArray.length())
                {
                    rand=rand-1;

                }
                if(phoneNo !=phoneNo2)
                {
                    String answer=jsonArray.getString(rand);
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo,null,answer,null,null);
                    Toast.makeText(context,"Message send to : "+phoneNo,Toast.LENGTH_SHORT).show();
                    state=false;
                    mainActivity.phoneNo2=phoneNo;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "No possible reply found", Toast.LENGTH_SHORT).show();
            }

        }

    }
    private void SendMess(String address,String text)
    {
        SmsManager smsManager=SmsManager.getDefault();
        try
        {
            smsManager.sendTextMessage(address,null,text,null,null);

        }
        catch (Exception e)
        {
            Toast.makeText(ctx, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public String ReadJson(Context context)
    {
        String json="";
        try {

            InputStream inputStream=context.getAssets().open("Data_info");
            int size=inputStream.available();
            byte[] buffer=new byte[size];
            inputStream.read(buffer);
            json=new String(buffer,"UTF-8");
            inputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return  json;
    }

}



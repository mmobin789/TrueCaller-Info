package com.magicbio.truename.adapters;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
import com.magicbio.truename.R;
import com.magicbio.truename.activeandroid.Contact;
import com.magicbio.truename.utils.AdUtils;
import com.magicbio.truename.utils.CommonAnimationUtils;
import com.magicbio.truename.utils.ContactUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;


/**
 * Created by Bilal on 12/5/2017.
 */

public class ContactsAdapter extends DynamicSearchAdapter<Contact> {

    private List<Contact> CallLogModelList;
    private int previousPosition = -1;

    public ContactsAdapter(List<Contact> CallLogModelList) {
        super(CallLogModelList);
        this.CallLogModelList = CallLogModelList;
        //Toast.makeText(context,""+smsList.size(),Toast.LENGTH_LONG).show();
    }

    public void showAd() {
        int adPosition = AdUtils.getRandomAdPositionForList(3, getItemCount());
        CallLogModelList.get(adPosition).showAd = true;
        notifyItemChanged(adPosition);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_row, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.callNumber(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
            }
        });
        holder.btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtils.openSmsApp(CallLogModelList.get(holder.getAdapterPosition()).getNumber());
            }
        });

        holder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact model = CallLogModelList.get(holder.getAdapterPosition());
                ContactUtils.shareLocationOnSms(model.getNumber(), model.getName());
            }
        });

        holder.btnwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact model = CallLogModelList.get(holder.getAdapterPosition());
                ContactUtils.openWhatsAppChat(model.getNumber());
            }
        });
        holder.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact model = CallLogModelList.get(holder.getAdapterPosition());
                ContactUtils.openCallDetailsActivity(model.getName(), model.getNumber(), model);
            }
        });

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Contact contact = CallLogModelList.get(position);
                if (previousPosition > -1) { // if previous opened close it
                    Contact contactOpened = CallLogModelList.get(previousPosition);
                    contactOpened.areOptionsShown = false;
                    notifyItemChanged(previousPosition);

                }
                // hidden so show
                CommonAnimationUtils.slideFromRightToLeft(holder.btnView, holder.rl.getWidth() - holder.img.getWidth());

                contact.areOptionsShown = true;

                previousPosition = position;
            }
        });
        return holder;
    }


    @Override
    public void search(@Nullable String s, @Nullable Function0<Unit> onNothingFound) {
        if (s != null && s.matches(Patterns.PHONE.pattern()))
            Contact.setSearchByNumber();
        else Contact.setSearchByName();


        super.search(s, onNothingFound);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        MyViewHolder holder = (MyViewHolder) vh;
        Contact contact = CallLogModelList.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtNumber.setText(contact.getNumber());

        if (contact.areOptionsShown)
            holder.btnView.setVisibility(View.VISIBLE);
        else {
            holder.btnView.setVisibility(View.GONE);
        }

        if (contact.showAd)
            holder.adView.setVisibility(View.VISIBLE);
        else holder.adView.setVisibility(View.GONE);

        Glide.with(holder.img).load(CallLogModelList.get(position).getImage()).apply(RequestOptions.errorOf(R.drawable.no_image)).into(holder.img);
//        Glide.with(context)
//                .load(smsList.get(position).getImage())
//                .centerCrop()
//                .into(holder.img);

        //holder.txtTimeAndN.setText(smsList.get(position).getNumber());

//        if(position%2==0)
//        {
//            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
//        }
//        else
//        {
//            holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
//        }

    }

    @Override
    public int getItemCount() {
        return CallLogModelList.size();
    }

  /*  public void editContact(String number) {
        Intent intentInsertEdit = new Intent(Intent.ACTION_INSERT_OR_EDIT, Uri.fromParts("tel", number, null));
        // Sets the MIME type
        intentInsertEdit.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        // Add code here to insert extended data, if desired
        // Sends the Intent with an request ID
        context.startActivity(intentInsertEdit);
    }*/

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView btnCall;
        ImageView img;
        AdView adView;
        RelativeLayout rl;
        LinearLayout btnView;
        Button btnSms, btnLocation, btnHistory, btnwa;

        MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnSms = itemView.findViewById(R.id.btnSms);
            btnLocation = itemView.findViewById(R.id.btnLocation);
            btnHistory = itemView.findViewById(R.id.btnHistory);
            btnwa = itemView.findViewById(R.id.btnwa);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            img = itemView.findViewById(R.id.img);
            adView = itemView.findViewById(R.id.adView);
            rl = itemView.findViewById(R.id.rl);
            btnView = itemView.findViewById(R.id.btnView);
            AdUtils.loadBannerAd(adView);
            // txtTimeAndN=itemView.findViewById(R.id.txtNumber);
        }
    }
}

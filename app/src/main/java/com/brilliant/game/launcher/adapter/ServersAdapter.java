package com.brilliant.game.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brilliant.game.R;
import com.brilliant.game.launcher.model.Servers;

import java.util.ArrayList;

/**
 * Чисто информационная лента серверов на главном экране — в отличие от x32,
 * здесь она НЕ управляет тем, к какому серверу реально подключается игра
 * (тот один и зашит напрямую в HomeActivity). Карточки только показывают
 * название/онлайн.
 */
public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServersViewHolder> {

    private final Context context;
    private final ArrayList<Servers> slist;

    public ServersAdapter(Context context, ArrayList<Servers> slist) {
        this.context = context;
        this.slist = slist;
    }

    @NonNull
    @Override
    public ServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_server, parent, false);
        return new ServersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServersViewHolder holder, int position) {
        Servers server = slist.get(position);

        holder.serverText.setText(server.getName());
        holder.onlineText.setText(String.valueOf(server.getOnline()));
        holder.maxOnlineText.setText("/" + server.getMaxOnline());
    }

    @Override
    public int getItemCount() {
        return slist.size();
    }

    public static class ServersViewHolder extends RecyclerView.ViewHolder {

        final TextView serverText;
        final TextView onlineText;
        final TextView maxOnlineText;

        public ServersViewHolder(View itemView) {
            super(itemView);

            serverText = itemView.findViewById(R.id.brp_launcher_server_title);
            onlineText = itemView.findViewById(R.id.brp_launcher_server_online);
            maxOnlineText = itemView.findViewById(R.id.brp_launcher_server_max_online);
        }
    }
}

package ie.ul.cs4084finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchResultAdapter extends FirestoreRecyclerAdapter<SearchResult,SearchResultAdapter.SearchResultHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SearchResultAdapter(@NonNull FirestoreRecyclerOptions<SearchResult> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchResultHolder holder, int position, @NonNull SearchResult model) {
        holder.advert_title.setText(model.getAdvertTitle());
        holder.advert_description.setText(model.getAdvertDescription());
        holder.advert_quality.setText(model.getAdvertQuality());
        holder.advert_price.setText(model.getAdvertPrice());
        //holder.advert_image.setImageIcon(model.getAdvertImage());
        // are these above coeewcr aka strigs an ints
    }

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result,
                parent,false);
        return new SearchResultHolder(v);
    }

    class SearchResultHolder extends RecyclerView.ViewHolder{

        TextView advert_price;
        TextView advert_quality;
        TextView advert_title;
        TextView advert_description;
        ImageView advert_image;


        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            advert_price = itemView.findViewById(R.id.advert_price);
            advert_quality = itemView.findViewById(R.id.advert_quality);
            advert_title = itemView.findViewById(R.id.advert_title);
            advert_description = itemView.findViewById(R.id.advert_description);
            advert_image = itemView.findViewById(R.id.advert_image);


        }
    }
}

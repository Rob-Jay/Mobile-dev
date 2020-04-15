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

    public SearchResultAdapter(@NonNull FirestoreRecyclerOptions<SearchResult> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchResultHolder holder, int position, @NonNull SearchResult model) {
        System.out.println("Created BindViewHolder");
        holder.title.setText(String.valueOf(model.getTitle()));
        holder.description.setText(String.valueOf(model.getDescription()));
        holder.quality.setText(String.valueOf(model.getQuality()));
        holder.price.setText(String.valueOf(model.getPrice()));
        //holder.image.setImageIcon(model.getAdvertImage());
        // are these above correct aka strings and ints
    }

    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("Created ViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result,
                parent,false);
        return new SearchResultHolder(v);
    }

    class SearchResultHolder extends RecyclerView.ViewHolder{
        TextView price;
        TextView quality;
        TextView title;
        TextView description;
        ImageView image;


        public SearchResultHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.advert_price);
            quality = itemView.findViewById(R.id.advert_quality);
            title = itemView.findViewById(R.id.advert_title);
            description = itemView.findViewById(R.id.advert_description);
            image = itemView.findViewById(R.id.advert_image);


        }
    }
}

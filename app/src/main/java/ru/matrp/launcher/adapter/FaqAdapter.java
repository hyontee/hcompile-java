package ru.matrp.launcher.adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.model.Faq;
import ru.matrp.bonus.R;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    Context context;

    public Activity activity;

    public FaqViewHolder faqViewHolder = null;

    ArrayList<Faq> faqlist;

    public FaqAdapter(Context context, ArrayList<Faq> faqlist) {
        this.context = context;
        this.faqlist = faqlist;
        activity = MainActivity.getMainActivity();
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false);
        return new FaqViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        Faq faq = faqlist.get(position);
        holder.faq_item_caption.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        holder.faq_item_arrow.setRotation(0.0f);
        holder.faq_item_text.setVisibility(View.VISIBLE);
        holder.faq_item_text.setMaxHeight(0);
        holder.faq_item_text.setScrollY(0);
        if (holder.valueAnimator != null) {
            holder.valueAnimator.removeAllListeners();
            holder.valueAnimator.removeAllUpdateListeners();
            holder.valueAnimator.cancel();
        }
        holder.bool = false;
        holder.afloat = 0.0f;
        holder.faq_item_caption.setText(faq.getCaption());
        StringBuilder sb = new StringBuilder();
        String str = String.format("<b>%s</b>", faq.getCaption());
        sb.append(str);
        SpannableString spannableString = new SpannableString(sb);
        spannableString.setSpan((Typeface.defaultFromStyle(Typeface.BOLD)), 0, spannableString.length(), 33);
        CharSequence con = TextUtils.concat(Html.fromHtml(spannableString.toString()), "\n\n");
        CharSequence concat = TextUtils.concat(con, Html.fromHtml(faq.getText()));
        holder.charSequence = concat;
        holder.faq_item_text.setText(concat);
        holder.faq_item_text.setMovementMethod(LinkMovementMethod.getInstance());
        holder.faq_item_text.setLinkTextColor(-7186975);
        holder.faq_item_text.setHighlightColor(0);
        holder.view.setOnTouchListener(new MainActivity.animClickBtn(context, holder.view));
        holder.view.setOnClickListener(new ClickFaqItem(this, holder));
    }

    @Override
    public int getItemCount() {
        return faqlist.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        public float afloat;
        public CharSequence charSequence;
        public final View view;
        public final TextView faq_item_caption;
        public final ImageView faq_item_arrow;
        public final TextView faq_item_text;
        public ValueAnimator valueAnimator;
        public boolean bool;

        public FaqViewHolder(View view) {
            super(view);
            new Handler(Looper.getMainLooper());
            valueAnimator = null;
            bool = false;
            afloat = 0.0f;
            this.view = view;
            faq_item_caption = (TextView) view.findViewById(R.id.faq_item_caption);
            faq_item_arrow = (ImageView) view.findViewById(R.id.faq_item_arrow);
            faq_item_text = (TextView) view.findViewById(R.id.faq_item_text);
        }
    }

    public final int textMess(CharSequence charSequence, float f10, int i10, Typeface typeface) {
        TextView textView = new TextView(context);
        textView.setPadding(context.getResources().getDimensionPixelSize(R.dimen._18sdp), context.getResources().getDimensionPixelSize(R.dimen._12sdp), context.getResources().getDimensionPixelSize(R.dimen._18sdp), context.getResources().getDimensionPixelSize(R.dimen._12sdp));
        textView.setTypeface(typeface);
        textView.setText(charSequence, TextView.BufferType.SPANNABLE);
        textView.setTextSize(0, f10);
        textView.measure(View.MeasureSpec.makeMeasureSpec(i10, 0x80000000), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return textView.getMeasuredHeight();
    }

    public final class ClickFaqItem implements View.OnClickListener {

        public final FaqViewHolder faqViewHolder1;

        public final FaqAdapter faqAdapter;

        public class C0229a implements ValueAnimator.AnimatorUpdateListener {

            public final FaqViewHolder faqViewHolder2;

            public C0229a(FaqViewHolder aVar) {
                faqViewHolder2 = aVar;
            }

            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                faqViewHolder2.afloat = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                faqViewHolder2.faq_item_arrow.setRotation(faqViewHolder2.afloat * (-180.0f));
                faqViewHolder2.faq_item_text.setMaxHeight((int) (faqViewHolder2.afloat * textMess(faqViewHolder2.charSequence, activity.getResources().getDimensionPixelSize(R.dimen._10sdp), faqViewHolder2.view.getWidth(), Typeface.defaultFromStyle(Typeface.NORMAL))));
                //faqViewHolder2.faq_item_text.setVisibility(View.GONE);
                faqViewHolder2.faq_item_text.setScrollY(0);
            }
        }

        public class AnimEndItem extends AnimatorListenerAdapter {

            public final FaqViewHolder faqViewHolder1;

            public AnimEndItem(FaqViewHolder aVar) {
                faqViewHolder1 = aVar;
            }

            @Override
            public final void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                faqViewHolder1.afloat = 0.0f;
                faqViewHolder1.valueAnimator.cancel();
                faqViewHolder1.valueAnimator = null;
            }
        }

        public class UpdateAnim implements ValueAnimator.AnimatorUpdateListener {
            public UpdateAnim() {
            }

            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                faqViewHolder1.afloat = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                faqViewHolder1.faq_item_arrow.setRotation(faqViewHolder1.afloat * (-180.0f));
                faqViewHolder1.faq_item_text.setMaxHeight((int) (faqViewHolder1.afloat * textMess(faqViewHolder1.charSequence, activity.getResources().getDimensionPixelSize(R.dimen._10sdp), faqViewHolder1.view.getWidth(), Typeface.defaultFromStyle(Typeface.NORMAL))));
                faqViewHolder1.faq_item_text.setScrollY(0);
            }
        }

        public class EndAnim extends AnimatorListenerAdapter {
            public EndAnim() {
            }

            @Override
            public final void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                faqViewHolder1.afloat = 1.0f;
                faqViewHolder1.valueAnimator.cancel();
                faqViewHolder1.valueAnimator = null;
            }
        }

        public ClickFaqItem(FaqAdapter faqAdapter3, FaqViewHolder faqViewHolder3) {
            faqAdapter = faqAdapter3;
            faqViewHolder1 = faqViewHolder3;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            if (faqViewHolder != null && faqViewHolder.bool) {
                faqViewHolder.faq_item_caption.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                if (faqViewHolder.valueAnimator != null) {
                    faqViewHolder.valueAnimator.removeAllListeners();
                    faqViewHolder.valueAnimator.removeAllUpdateListeners();
                    faqViewHolder.valueAnimator.cancel();
                }
                faqViewHolder.valueAnimator = ValueAnimator.ofFloat(faqViewHolder.afloat, 0.0f);
                faqViewHolder.valueAnimator.addUpdateListener(new C0229a(faqViewHolder));
                faqViewHolder.valueAnimator.addListener(new AnimEndItem(faqViewHolder));
                faqViewHolder.valueAnimator.setInterpolator(new LinearInterpolator());
                faqViewHolder.valueAnimator.setDuration((long) (faqViewHolder.afloat * 400.0f));
                faqViewHolder.valueAnimator.start();
                faqViewHolder.bool = false;
                if (faqViewHolder == faqViewHolder1) {
                    faqViewHolder = null;
                    return;
                }
                faqViewHolder = null;
            }
            faqViewHolder = faqViewHolder1;
            faqViewHolder1.faq_item_caption.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            if (faqViewHolder1.valueAnimator != null) {
                faqViewHolder1.valueAnimator.removeAllListeners();
                faqViewHolder1.valueAnimator.removeAllUpdateListeners();
                faqViewHolder1.valueAnimator.cancel();
            }
            faqViewHolder1.valueAnimator = ValueAnimator.ofFloat(faqViewHolder1.afloat, 1.0f);
            faqViewHolder1.valueAnimator.addUpdateListener(new UpdateAnim());
            faqViewHolder1.valueAnimator.addListener(new EndAnim());
            faqViewHolder1.valueAnimator.setInterpolator(new LinearInterpolator());
            faqViewHolder1.valueAnimator.setDuration((long) ((1.0f - faqViewHolder1.afloat) * 400.0f));
            faqViewHolder1.valueAnimator.start();
            faqViewHolder1.bool = true;
        }
    }
}

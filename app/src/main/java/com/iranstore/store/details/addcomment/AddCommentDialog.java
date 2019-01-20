package com.iranstore.store.details.addcomment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.iranstore.store.model.Comment;
import com.iranstore.store.model.api.SsSingleObserver;
import com.iranstore.store.BaseActivity;
import com.ss.sevenstore.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AddCommentDialog extends DialogFragment {
    private AddCommentViewModel viewModel;
    private int productId;
    private EditText commentTitleEt;
    private EditText commentContentEt;
    private Button addCommentBtn;
    private Button cancelBtn;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productId = getArguments().getInt("product_id");
        if (productId == 0) {
            dismiss();
        }
        viewModel = new AddCommentViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_comment, null);
        commentTitleEt = view.findViewById(R.id.et_addComment_title);

        commentContentEt = view.findViewById(R.id.et_addComment_content);

        cancelBtn = view.findViewById(R.id.button_addComment_cancel);
        cancelBtn.setOnClickListener(v -> dismiss());

        addCommentBtn = view.findViewById(R.id.button_addComment);
        addCommentBtn.setOnClickListener(v -> {
            if (commentTitleEt.length() > 0 && commentContentEt.length() > 0) {
                viewModel.addComment(productId, commentTitleEt.getText().toString(),
                        commentContentEt.getText().toString())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SsSingleObserver<Comment>((BaseActivity) getActivity()) {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(Comment comment) {
                                ((BaseActivity) getActivity()).showSnackBarMessage("نظر شما با موفقیت ثبت شد، پس از تایید مدیر منتشر خواهد شد");
                                dismiss();
                            }
                        });
            } else {
                if (commentTitleEt.length() == 0) {
                    commentTitleEt.setError("عنوان نظر نمی تواند خالی باشد");
                } else {
                    commentContentEt.setError("متن نظر نمی تواند خالی باشد");
                }
            }
        });

        progressBar = view.findViewById(R.id.progressBar_addComment);
        Disposable disposable = viewModel.progressBarVisibility().subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
                    addCommentBtn.setVisibility(aBoolean ? View.INVISIBLE : View.VISIBLE);
                });
        compositeDisposable.add(disposable);

        builder.setView(view);
        return builder.create();
    }

    public static AddCommentDialog newInstance(int productId) {

        Bundle args = new Bundle();
        args.putInt("product_id", productId);
        AddCommentDialog fragment = new AddCommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }
}

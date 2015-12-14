package Views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cfy.project3.R;

/**
 * Created by cfy on 15-12-14.
 */
public class InputDialogue extends Dialog{
    private Button btn_enter = null;
    private Button btn_cancel = null;
    private TextView text_title = null;
    private EditText edittext_content = null;

    public InputDialogue(Context context) {
        super(context,R.style.Theme_server_InputDialogue);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_inputdialogue,null);
        btn_enter = (Button) v.findViewById(R.id.btn_enter);
        btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
        text_title = (TextView) v.findViewById(R.id.textTitle);
        edittext_content = (EditText) v.findViewById(R.id.edittext_content);

        setContentView(v);
    }

    public void setOnEnterClickListener(View.OnClickListener l){
        btn_enter.setOnClickListener(l);
    }
    public void setOnCancelClickListener(View.OnClickListener l){
        btn_cancel.setOnClickListener(l);
    }

    public String getText(){
        return edittext_content.getText().toString();
    }
    public void setTitle(String title){
        this.text_title.setText(title);
    }
}

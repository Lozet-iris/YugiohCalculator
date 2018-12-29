/*
* 開発履歴
* 2018/03/24 開発履歴の作成を開始
*            戻るボタン実装
*            計算途中でプレイヤーを切り替えた際にライフ表示が計算中のままになる不具合を修正
*            アクティブプレイヤーの強調表示を暫定的に実装
* 2018/03/24 画面レイアウトを調整、ライフ表示位置の横幅が等しくなるように変更
*/

package com.example.lozet.yugiohcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import java.util.ArrayList;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    //共通変数
    public static final int intDefaultLife = 8000;    //初期ライフ
    public static final int intLifeDigit = 5;  //ライフの入力可能上限桁数(Not表示可能桁数)
    public static int intActPlayer = 1;        //アクティブプレイヤー
    public static int intLife[] = new int[2];  //ライフポイント管理用
    public static ArrayList<Integer> aryLifeLog1 = new ArrayList<Integer>(); //ライフ変動記録 P1
    public static ArrayList<Integer> aryLifeLog2 = new ArrayList<Integer>(); //ライフ変動記録 P2
    public static ArrayList<Integer> aryTargetLog = new ArrayList<Integer>(); //対象プレイヤー記録(1:1P、2:2P、3:両方)

    //起動時処理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //内部管理用のライフポイントを初期化
        for(int i = 1; i < intLife.length; i++){
            intLife[i] = intDefaultLife;
        }
        aryLifeLog1.add(intDefaultLife);
        aryLifeLog2.add(intDefaultLife);
        aryTargetLog.add(3);

        //背景色を設定
        TextView objTextView =  (TextView)findViewById(R.id.LifePointP1);
        objTextView.setBackgroundColor(Color.parseColor("cyan"));
        objTextView =  (TextView)findViewById(R.id.LifePointP2);
        objTextView.setBackgroundColor(Color.parseColor("lightgray"));
    }

    //メインメソッド
    public static void main(String args[]){

    }

    /*
    * イベントハンドラ用メソッド
    */
    //プレイヤーボタン
    public void txtPL1(View btnView){PlayerSelect(1);}
    public void txtPL2(View btnView){PlayerSelect(2);}

    //数字ボタン
    public void btn1(View btnView){
        Culc("1");
    }
    public void btn2(View btnView){
        Culc("2");
    }
    public void btn3(View btnView){
        Culc("3");
    }
    public void btn4(View btnView){
        Culc("4");
    }
    public void btn5(View btnView){
        Culc("5");
    }
    public void btn6(View btnView){
        Culc("6");
    }
    public void btn7(View btnView){
        Culc("7");
    }
    public void btn8(View btnView){
        Culc("8");
    }
    public void btn9(View btnView){
        Culc("9");
    }
    public void btn0(View btnView){
        Culc("0");
    }
    public void btn00(View btnView){
        Culc("00");
    }
    public void btn000(View btnView){
        Culc("000");
    }
    public void btnPlus(View btnView){
        Culc("+");
    }
    public void btnMinus(View btnView){
        Culc("-");
    }

    //イコールボタン
    public void btnEqual(View btnView){
        Equal();
    }

    //リセットボタン
    public void btnLifeReset(View btnView){
        ResetButton(Boolean.TRUE);
    }

    //戻るボタン
    public void btnReturn(View btnView){
        Return();
    }
    /*
    * 実処理部メソッド
    */

    //数値、演算子ボタン処理
    public void Culc(String strPressButton){
        //変数
        String strLifePoint;   //ライフ管理用
        TextView objTextView = null;   //プレイヤーのライフ表示オブジェクト
        StringBuilder objStrBld = new StringBuilder();      //文字列構築用オブジェクト

        try{
            //プレイヤーのライフ情報を取得
            switch(intActPlayer){
                case 1:
                    //プレイヤー1
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    break;
                case 2:
                    //プレイヤー2
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    break;
                default:
                    break;
            }
            strLifePoint = objTextView.getText().toString();

            //押下ボタンが0系であり、計算中の数値が未入力なら処理は行わない
            if(strPressButton.substring(0,1).equals("0")){
                if(isNumber(strLifePoint.substring(0,1))){
                    //符号無しの場合
                    return;
                }else{
                    //符号ありの場合
                    if(strLifePoint.length() == 1){
                        return;
                    }
                }
            }

            //画面の数値が記録と異なるなら、初回呼び出し時のみ共通変数に記録する
            if(isNumber(strLifePoint.substring(0,1))){
                if(!(strLifePoint == String.valueOf(intLife[intActPlayer - 1]))){
                    intLife[intActPlayer - 1] = Integer.parseInt(strLifePoint);
                }
            }

            //符号調整
            if(isNumber(strLifePoint.substring(0,1))){

                //元の符号が無い場合
                if(isNumber(strPressButton)){
                    objStrBld.append("-");
                }else{
                    objStrBld.append(strPressButton);
                }

            }else{

                //元の符号が有る場合
                if(isNumber(strPressButton)){
                    objStrBld.append(strLifePoint.substring(0,1));
                }else{
                    objStrBld.append(strPressButton);
                }
                strLifePoint = strLifePoint.substring(1,strLifePoint.length());
            }

            //二度目以降の入力時は前回入力数値を引き継ぐ
            if(!isNumber(objTextView.getText().toString().substring(0,1))){
                objStrBld.append(strLifePoint);
            }

            //数値入力時は末尾に追加
            if(isNumber(strPressButton)){
                objStrBld.append(strPressButton);
            }

            //桁数超過で無いときのみ画面に反映する
            if(!(objStrBld.toString().length() > intLifeDigit)){
                objTextView.setText(objStrBld.toString());
            }

        }catch (Exception e){
            System.out.println("例外エラー発生");
            System.out.println(e);
        }
    }

    //イコールボタン処理
    public void Equal(){
        //変数
        String strLifePoint;    //ライフ管理用
        TextView objTextView = null;   //プレイヤーのライフ表示オブジェクト
        StringBuilder objStrBld = new StringBuilder();      //文字列構築用オブジェクト

        try{
            //プレイヤーのライフ情報を取得
            switch(intActPlayer) {
                case 1:
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    break;
                case 2:
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    break;
                 default:
                     return;
            }
            strLifePoint = objTextView.getText().toString();

            //数値入力前なら処理はしない
            if(isNumber(strLifePoint.substring(0,1)) || strLifePoint.length() == 0){
                return;
            }

            //桁数自動調整機能
            objStrBld.append(strLifePoint);
            if(strLifePoint.length() < 4){
                if(strLifePoint.length() < 3){
                    //数値1桁のときは100ポイント台に変換
                    objStrBld.append("00");
                }else{
                    //数値2桁のときは1000ポイント台に変換、ただし50のみ例外的に許可
                    if(!strLifePoint.substring(1,3).equals("50")){
                        objStrBld.append("00");
                    }
                }
            }
            strLifePoint = objStrBld.toString();

            //演算処理して変数に格納
            switch (strLifePoint.substring(0,1)){
                case "+":
                    intLife[intActPlayer - 1] += Integer.parseInt(strLifePoint.substring(1,strLifePoint.length()));
                    break;
                case "-":
                    intLife[intActPlayer - 1] -= Integer.parseInt(strLifePoint.substring(1,strLifePoint.length()));
                    break;
            }

            //計算結果0未満は0として扱う
            if(intLife[intActPlayer - 1] < 0){
                intLife[intActPlayer - 1] = 0;
            }

            //計算結果100000以上は99999として扱う
            if(intLife[intActPlayer - 1] > 99999){
                intLife[intActPlayer - 1] = 99999;
            }

            //ライフを画面にセット
            objTextView.setText(String.valueOf(intLife[intActPlayer - 1]));

            //ログを取得
            GetLog(intActPlayer);

        }catch (Exception e){
            System.out.println("例外エラー発生");
            System.out.println(e);
        }
    }

    //リセットボタン処理
    //bolDialogFlg : Trueでメッセージを表示
    public void ResetButton(Boolean bolDialogFlg){
        try{
            if(bolDialogFlg){
                //ダイアログ内容の設定
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("確認");
                builder.setMessage("ライフをリセットします。よろしいですか？");

                // ダイアログのボタン定義
                builder.setNegativeButton("はい",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Reset();
                            }
                        });
                builder.setPositiveButton("いいえ",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog dialog = builder.create();

                //ダイアログを表示
                dialog.show();
            }else{
                Reset();
            }

        }catch (Exception e){
            System.out.println("例外エラー発生");
            System.out.println(e);
        }
    }

    //リセット処理
    public void Reset(){
        //ライフ初期値設定
        for(int i = 1; i <= intLife.length; i++){
            intLife[i - 1] = intLifeDigit;
        }
        aryLifeLog1.clear();
        aryLifeLog1.add(intDefaultLife);
        aryLifeLog2.clear();
        aryLifeLog2.add(intDefaultLife);
        aryTargetLog.clear();
        aryTargetLog.add(3);

        //ライフ表示初期化処理
        TextView textSetting = (TextView)findViewById(R.id.LifePointP1);
        textSetting.setText(String.valueOf(intDefaultLife));
        textSetting = (TextView)findViewById(R.id.LifePointP2);
        textSetting.setText(String.valueOf(intDefaultLife));

        //プレイヤー1を選択
        PlayerSelect(1);
    }

    //プレイヤーボタン処理
    public void PlayerSelect(int PlayerID){
        //切り替え前プレイヤーへの処理
        TextView objTextView = null;
        switch(intActPlayer){
            case 1:
                //プレイヤー1
                //計算処理中の場合を考慮し、ライフ表示をリセットする
                objTextView = (TextView)findViewById(R.id.LifePointP1);
                objTextView.setText(String.valueOf(aryLifeLog1.get(aryLifeLog1.size() - 1)));

                //背景色をクリア
                objTextView.setBackgroundColor(Color.parseColor("lightgray"));
                break;

            case 2:
                //プレイヤー2
                //計算処理中の場合を考慮し、ライフ表示をリセットする
                objTextView = (TextView)findViewById(R.id.LifePointP2);
                objTextView.setText(String.valueOf(aryLifeLog2.get(aryLifeLog2.size() - 1)));

                //背景色をクリア
                objTextView.setBackgroundColor(Color.parseColor("lightgray"));
                break;

            default:
                //該当無しの時の処理
                Return();
        }

        //アクティブプレイヤーを設定
        intActPlayer = PlayerID;

        //切り替え後プレイヤーへの処理
        switch(intActPlayer){
            case 1:
                //プレイヤー1
                //背景色を設定
                objTextView = (TextView)findViewById(R.id.LifePointP1);
                objTextView.setBackgroundColor(Color.parseColor("cyan"));
                break;

            case 2:
                //プレイヤー2
                //背景色を設定
                objTextView = (TextView)findViewById(R.id.LifePointP2);
                objTextView.setBackgroundColor(Color.parseColor("cyan"));
                break;

            default:
                //該当無しの時の処理
                Return();
        }
    }

    //戻るボタン処理
    public void Return(){
        try{
            //変数宣言
            TextView objTextView;

            //ターゲット別処理
            switch(aryTargetLog.get(aryTargetLog.size() - 1)){
                case 1:
                    //プレイヤー1
                    if(!(aryLifeLog1.size() == 1)){
                        aryLifeLog1.remove(aryLifeLog1.size() - 1);
                    }
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    objTextView.setText(String.valueOf(aryLifeLog1.get(aryLifeLog1.size() - 1)));
                    break;

                case 2:
                    //プレイヤー2
                    if(!(aryLifeLog2.size() == 1)){
                        aryLifeLog2.remove(aryLifeLog2.size() - 1);
                    }
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    objTextView.setText(String.valueOf(aryLifeLog2.get(aryLifeLog2.size() - 1)));
                    break;

                case 3:
                    //両方
                    if(!(aryLifeLog1.size() == 1)){
                        aryLifeLog1.remove(aryLifeLog1.size() - 1);
                    }
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    objTextView.setText(String.valueOf(aryLifeLog1.get(aryLifeLog1.size() - 1)));

                    if(!(aryLifeLog2.size() == 1)){
                        aryLifeLog2.remove(aryLifeLog2.size() - 1);
                    }
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    objTextView.setText(String.valueOf(aryLifeLog2.get(aryLifeLog2.size() - 1)));
                    break;

                default:
                    //該当無しの時の処理
                    Return();
            }

            //対象情報を1つ戻す
            if(!(aryTargetLog.size() == 1)){
                aryTargetLog.remove(aryTargetLog.size() - 1);
            }

        }catch (Exception e){
            System.out.println("例外エラー発生");
            System.out.println(e);
        }
    }

    //変動ログの取得(現在のライフを自動で取得)
    public void GetLog(int intTarget){
        try{
            //変数宣言
            TextView objTextView = null;

            //ターゲット別処理
            switch(intTarget){
                case 1:
                    //プレイヤー1
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    aryLifeLog1.add(Integer.parseInt(objTextView.getText().toString()));
                    break;

                case 2:
                    //プレイヤー2
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    aryLifeLog2.add(Integer.parseInt(objTextView.getText().toString()));
                    break;

                case 3:
                    //両方
                    objTextView = (TextView)findViewById(R.id.LifePointP1);
                    aryLifeLog1.add(Integer.parseInt(objTextView.getText().toString()));
                    objTextView = (TextView)findViewById(R.id.LifePointP2);
                    aryLifeLog2.add(Integer.parseInt(objTextView.getText().toString()));
                    break;

                default:
                    //該当無しの時の処理
                    Return();
            }

            //ログをセット
            aryTargetLog.add(intTarget);

        }catch (Exception e){
            System.out.println("例外エラー発生");
            System.out.println(e);
        }
    }

    /*
    * 共通関数
    */

    //数値判定
    public boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
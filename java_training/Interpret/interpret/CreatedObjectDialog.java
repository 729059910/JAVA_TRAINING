/*
 * 第16章練習問題について

練習問題16.6、16.7、16.8、16.10をそれぞれ作成する代わりに、Interpretプログラムを１つ作成する。
練習問題で指定された操作ができること。更に、以下のことも考慮して作成する。

GUIで作成する（AWT/Swingのどちらでも良い）
自分自身を起動できるようにする
java.awt.FrameのsetVisible()、setTitle()、setSize()、setBackground()を呼び出すデモができること
private finalのインスタンスフィールドの書き換えもできること

なお、Interpretの課題は、私からOKがでる何度も再提出（多分、3回が限度）してもらい確認を行います。
OKが出ない場合には、受講資格を失うこともありますので、注意してください。

課題提出時には、「JPL」や「GUI」のフォルダと同じ階層に「Interpret」というフォルダを作成し、
その中にコードを入れて提出する。
 */

/*
 * 練習問題16.10 p.378
 * Interpretをさらに修正して、ユーザが生成する配列の型とサイズを指定できて、その配列の要素を
 * 読みだしたり設定したりできて、また、配列の要素として含まれているオブジェクトを指定して、
 * そのオブジェクトのフィールドにアクセスしたりメソッドを呼び出したりできるようにしなさい。
 */

/*
 * 練習問題16.8 p.372
 * Interpretプログラムをさらに修正して、任意のクラスのコンストラクタをユーザが呼び出せるようにしなさい。
 * その際にどんな例外も表示しなさい。
 * また、オブジェクトの生成が成功したら、そのオブジェクトのメソッドをユーザが呼び出せるようにしなさい。
 */

/*
 * 練習問題16.7 p.370
 * オブジェクトに対してメソッドを呼び出すようにInterpretプログラムを修正しなさい。
 * 戻り値やスローされた例外を適切に表示するようにしなさい。
 */

/*
 * 練習問題16.6 p.368
 * 要求された型のオブジェクトを生成し、ユーザがそのオブジェクトのフィールドを調べて、
 * フィールドを修正できるInterpretプログラムを作成しなさい
 */

// TODO: 配列を引数に渡せるようにする
// TODO: intとかの配列も作れるようにする？

package interpret;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.*;

public class CreatedObjectDialog extends Dialog implements ActionListener
{
    private static final long serialVersionUID = 1L;
    // private GridBagLayout gbl = new GridBagLayout();
    // private GridBagConstraints gbc = new GridBagConstraints();

    private Interpret interpret;
    private Object createdObject;
    private Class<?> c;
    private Field[] field = new Field[500];
    private Method[] method = new Method[500];
    private Object[] methodArgument = new Object[500];

    private Label fieldLabel = new Label("Field: ");
    private Choice choiceField = new Choice();
    private Label selectFieldLabel = new Label("Select field: ");
    private Button getFieldValueButton = new Button("Get field value");
    private Label fieldValueLabel = new Label();
    private Label inputFieldValueLabel = new Label("Input field value: ");
    private TextArea inputFieldValueTextArea = new TextArea();
    private Button setFieldValueButton = new Button("Set field value");
    private Label methodLabel = new Label("Method: ");
    private Choice choiceMethod = new Choice();
    private Button selectMethodButton = new Button("Select method");
    private Label selectMethodArgumentLabel = new Label("Select method argument: ");
    private Choice choiceMethodArgument = new Choice();
    private Label inputMethodArgument = new Label("Input method argument");
    private TextArea inputMethodArgumentTextArea = new TextArea();
    private Button setMethodArgumentButton = new Button("Set method argument");
    private Choice setMethodArgumentBooleanChoice = new Choice();
    private Button setMethodArgumentBooleanButton = new Button("Set method argument boolean");
    private Choice setMethodArgumentObjectChoice = new Choice();
    private Button setMethodArgumentObjectButton = new Button("Set method argument object");
    private Button setMethodArgumentObjectCheckButton = new Button("Check created object");
    private Label invokeMethodLabel = new Label("Invoke Method");
    private Button invokeMethodButton = new Button("Invoke method");
    private Label returnValueOfMethodLabel = new Label("Return value of method: ");
    private Label returnValueOfMethodValueLabel = new Label();
    private Label errorLabel = new Label("Error message is shown here: ");
    private Label errorValueLabel = new Label();

    public CreatedObjectDialog(Frame owner, Object ar_createdObject, Class<?> ar_c)
    {
        super(owner);
        interpret = (Interpret)owner;
        createdObject = ar_createdObject;
        c = ar_c;


        // ウィンドウを閉じられるようにする
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        this.setTitle(interpret.getC().toString());
        this.setSize(800, 640);
        this.setResizable(true);

        // レイアウトの設定
        setLayoutWithGridLayout();
        // setLayoutWithGridBagLayout();
    }

    private void addFieldChoiceObjectDialog()
    {
        int i = 0;
        for (i = 0; i < c.getFields().length; i++)
        {
            field[i] = c.getFields()[i];
            choiceField.add(field[i].toString());
        }
        declaredFieldLabel: for (int j = 0; j < c.getDeclaredFields().length; j++)
        {
            for (int k =0; k < i; k++)
            {
                if (c.getDeclaredFields()[j].toString().equals(field[k].toString()))
                {
                    continue declaredFieldLabel;
                }
            }
            field[i] = c.getDeclaredFields()[j];
            choiceField.add(field[i].toString());
            i++;
        }

    }

    private void addMethodChoiceObjectDialog()
    {
        int i = 0;
        for (i = 0; i < c.getMethods().length; i++)
        {
            method[i] = c.getMethods()[i];
            choiceMethod.add(method[i].toString());
        }
        declaredMethodLabel: for (int j = 0; j < c.getDeclaredMethods().length; j++)
        {
            for (int k =0; k < i; k++)
            {
                if (c.getDeclaredMethods()[j].toString().equals(method[k].toString()))
                {
                    continue declaredMethodLabel;
                }
            }
            method[i] = c.getDeclaredMethods()[j];
            choiceMethod.add(method[i].toString());
            i++;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // getFieldValueButton
        if ("Get field value" == e.getActionCommand())
        {
            field[choiceField.getSelectedIndex()].setAccessible(true);
            try
            {
                fieldValueLabel.setText(field[choiceField.getSelectedIndex()].get(createdObject).toString());
            }
            catch(Exception ex)
            {
                System.out.println(ex);
            }
        }

        // setFieldValueButton
        if ("Set field value" == e.getActionCommand())
        {
            field[choiceField.getSelectedIndex()].setAccessible(true);
            try
            {
                // 基本型でString以外のものは変換してから代入
                if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("byte"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, Byte.valueOf(inputFieldValueTextArea.getText()));
                }
                else if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("short"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, Short.valueOf(inputFieldValueTextArea.getText()));
                }
                else if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("int"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, (int)Integer.valueOf(inputFieldValueTextArea.getText()));
                }
                else if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("long"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, Long.valueOf(inputFieldValueTextArea.getText()));
                }
                else if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("float"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, Float.valueOf(inputFieldValueTextArea.getText()));
                }
                else if (field[choiceField.getSelectedIndex()].getGenericType().toString().equals("double"))
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, Double.valueOf(inputFieldValueTextArea.getText()));
                }
                else
                {
                    field[choiceField.getSelectedIndex()].set(createdObject, inputFieldValueTextArea.getText());
                }

            }
            catch(Exception ex)
            {
                System.out.println(ex);
            }
        }

        // slectMethodButton
        if ("Select method" == e.getActionCommand())
        {
            // 初期化
            choiceMethodArgument.removeAll();
            for (int i = 0; i < methodArgument.length; i++)
            {
                methodArgument[i] = null;
            }

            for (int i = 0; i < method[choiceMethod.getSelectedIndex()].getGenericParameterTypes().length; i++)
            {
                choiceMethodArgument.add(method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[i].toString());
            }
        }

        // setMethodArgumentButon
        if ("Set method argument" == e.getActionCommand())
        {
            // 基本型でString以外のものは変換してから代入
            if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("byte"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Byte.valueOf(inputMethodArgumentTextArea.getText());
            }
            else if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("short"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Short.valueOf(inputMethodArgumentTextArea.getText());
            }
            else if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("int"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Integer.valueOf(inputMethodArgumentTextArea.getText());
            }
            else if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("long"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Long.valueOf(inputMethodArgumentTextArea.getText());
            }
            else if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("float"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Float.valueOf(inputMethodArgumentTextArea.getText());
            }
            else if (method[choiceMethod.getSelectedIndex()].getGenericParameterTypes()[choiceMethodArgument.getSelectedIndex()].toString().equals("double"))
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = Double.valueOf(inputMethodArgumentTextArea.getText());
            }
            else
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = inputMethodArgumentTextArea.getText();
            }
        }

        // setMethodArgumentBoolButton
        if ("Set method argument boolean" == e.getActionCommand())
        {
            if (0 == setMethodArgumentBooleanChoice.getSelectedIndex())
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = true;
            }
            else if (1 == setMethodArgumentBooleanChoice.getSelectedIndex())
            {
                methodArgument[choiceMethodArgument.getSelectedIndex()] = false;
            }
            else
            {
                errorLabel.setText("bool set error");
            }
        }

        // Set method argument object
        if ("Set method argument object" == e.getActionCommand())
        {
            methodArgument[choiceMethodArgument.getSelectedIndex()] = interpret.createdObject[setMethodArgumentObjectChoice.getSelectedIndex()];
        }

        // Check created object
        if ("Check created object" == e.getActionCommand())
        {
            System.out.println("!");
            setMethodArgumentObjectChoice.removeAll();
            for (int i = 0; i < interpret.createdObject.length; i++)
            {
                if (null == interpret.createdObject[i])
                {
                    break;
                }
                setMethodArgumentObjectChoice.add(interpret.objectName[i]);
            }

        }

        // invokeMethodButton
        if ("Invoke method" == e.getActionCommand())
        {
            // 実際に必要な長さの引数の配列を作る
            Object[] actualMethodArgument = new Object[method[choiceMethod.getSelectedIndex()].getGenericParameterTypes().length];
            for (int i = 0; i < actualMethodArgument.length; i++)
            {
                actualMethodArgument[i] = methodArgument[i];
            }
            try
            {
                // returnValueOfMethodValueLabel.setText(method[choiceMethod.getSelectedIndex()].invoke(createdObject, actualMethodArgument).toString());
                System.out.println("1");
                Method tmpmethod = method[choiceMethod.getSelectedIndex()];
                System.out.println("2");
                if (createdObject == null)
                {
                    System.out.println("!");
                }
                String tmp = tmpmethod.invoke(createdObject, actualMethodArgument).toString();
                System.out.println("3");
                returnValueOfMethodValueLabel.setText(tmp);
            }
            catch(Exception ex)
            {
                errorLabel.setText("Invoke method: " + ex.toString());
            }
        }
    }





    private void setLayoutWithGridLayout()
    {
        this.setLayout(new GridLayout(12, 3));

        // フィールド一覧
        this.add(fieldLabel);
        addFieldChoiceObjectDialog();
        this.add(choiceField);
        this.add(new Label(""));

        // フィールドの値取得
        this.add(selectFieldLabel);
        this.add(getFieldValueButton);
        getFieldValueButton.addActionListener(this);
        this.add(fieldValueLabel);

        // フィールドの値変更値入力
        this.add(inputFieldValueLabel);
        this.add(inputFieldValueTextArea);
        this.add(setFieldValueButton);
        setFieldValueButton.addActionListener(this);

        // メソッド一覧
        this.add(methodLabel);
        addMethodChoiceObjectDialog();
        this.add(choiceMethod);
        this.add(selectMethodButton);
        selectMethodButton.addActionListener(this);

        // メソッドの引数一覧
        this.add(selectMethodArgumentLabel);
        this.add(choiceMethodArgument);
        this.add(new Label(""));


        // メソッドの引数入力
        this.add(inputMethodArgument);
        this.add(inputMethodArgumentTextArea);
        this.add(setMethodArgumentButton);
        setMethodArgumentButton.addActionListener(this);

        // メソッドの引数入力（bool）
        this.add(new Label(""));
        setMethodArgumentBooleanChoice.add("true");
        setMethodArgumentBooleanChoice.add("false");
        this.add(setMethodArgumentBooleanChoice);
        this.add(setMethodArgumentBooleanButton);
        setMethodArgumentBooleanButton.addActionListener(this);

        // メソッドの引数object選択
        this.add(new Label(""));
        for (int i = 0; i < interpret.createdObject.length; i++)
        {
            if (null == interpret.createdObject[i])
            {
                break;
            }
            setMethodArgumentObjectChoice.add(interpret.objectName[i]);
        }
        this.add(setMethodArgumentObjectChoice);
        this.add(setMethodArgumentObjectButton);
        setMethodArgumentObjectButton.addActionListener(this);

        // メソッドの引数objectチェック
        this.add(new Label(""));
        this.add(new Label(""));
        this.add(setMethodArgumentObjectCheckButton);
        setMethodArgumentObjectCheckButton.addActionListener(this);

        // メソッド起動ボタン
        this.add(invokeMethodLabel);
        this.add(invokeMethodButton);
        invokeMethodButton.addActionListener(this);
        this.add(new Label(""));

        // メソッドの戻り値表示
        this.add(returnValueOfMethodLabel);
        this.add(returnValueOfMethodValueLabel);
        this.add(new Label(""));

        // エラーメッセージ表示
        this.add(errorLabel);
        this.add(errorValueLabel);
        this.add(new Label(""));

        this.setVisible(true);
    }

    /*
    private void setLayoutWithGridBagLayout()
    {
        this.setLayout(gbl);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(fieldLabel, gbc);
        this.add(fieldLabel);
        addFieldChoiceObjectDialog();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(choiceField, gbc);
        this.add(choiceField);

        // フィールドの値取得
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(selectFieldLabel, gbc);
        this.add(selectFieldLabel);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(getFieldValueButton, gbc);
        this.add(getFieldValueButton);
        getFieldValueButton.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(fieldValueLabel, gbc);
        this.add(fieldValueLabel);

        // フィールドの値変更値入力
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(inputFieldValueLabel, gbc);
        this.add(inputFieldValueLabel);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(inputFieldValueTextArea, gbc);
        this.add(inputFieldValueTextArea);
        getFieldValueButton.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(setFieldValueButton, gbc);
        this.add(setFieldValueButton);
        setFieldValueButton.addActionListener(this);

        // メソッド一覧
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(methodLabel, gbc);
        this.add(methodLabel);
        addMethodChoiceObjectDialog();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(choiceMethod, gbc);
        this.add(choiceMethod);
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(selectMethodButton, gbc);
        this.add(selectMethodButton);
        selectMethodButton.addActionListener(this);

        // メソッドの引数一覧
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(selectMethodArgumentLabel, gbc);
        this.add(selectMethodArgumentLabel);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(choiceMethodArgument, gbc);
        this.add(choiceMethodArgument);


        // メソッドの引数入力
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(inputMethodArgument, gbc);
        this.add(inputMethodArgument);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(inputMethodArgumentTextArea, gbc);
        this.add(inputMethodArgumentTextArea);
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(setMethodArgumentButton, gbc);
        this.add(setMethodArgumentButton);
        selectMethodButton.addActionListener(this);
        setMethodArgumentButton.addActionListener(this);

        // メソッドの引数入力（bool）
        setMethodArgumentBooleanChoice.add("true");
        setMethodArgumentBooleanChoice.add("false");
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(setMethodArgumentBooleanChoice, gbc);
        this.add(setMethodArgumentBooleanChoice);
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(setMethodArgumentBooleanButton, gbc);
        this.add(setMethodArgumentBooleanButton);
        setMethodArgumentBooleanButton.addActionListener(this);

        // メソッド起動ボタン
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(invokeMethodLabel, gbc);
        this.add(invokeMethodLabel);
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(invokeMethodButton, gbc);
        this.add(invokeMethodButton);
        invokeMethodButton.addActionListener(this);

        // メソッドの戻り値表示
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(returnValueOfMethodLabel, gbc);
        this.add(returnValueOfMethodLabel);
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(returnValueOfMethodValueLabel, gbc);
        this.add(returnValueOfMethodValueLabel);

        // エラーメッセージ表示
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbl.setConstraints(errorLabel, gbc);
        this.add(errorLabel);
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbl.setConstraints(errorValueLabel, gbc);
        this.add(errorValueLabel);

        this.setVisible(true);
    }
    */

}
package com.ttxgps.gpslocation;
//package com.car.carlocation;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.text.InputType;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.car.carlocation.R;
//import com.car.entity.Item;
//import com.car.utils.MySSLSocketFactory;
//import com.car.utils.Urls;
//import com.palmtrends.loadimage.Utils;
//import com.utils.JniUtils;
//import com.ttxgps.utils.PrefHelper;
//
//public class PeopleControllActivity extends BaseActivity {
//	public static String[] CAR_STATUS = { "�����", "δ���", "����", "�ϵ�", "�˶�",
//			"ACC��", "���ſ�", "���Ŷ�", "���Ŷ�", "�����", "�ڼ�", "���" };
//
//	Item item;
//	Button lock;
//	Button breaks;
//	public static final int success = 3;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_peoplecontrol);
//		item = (Item) getIntent().getSerializableExtra("item");
//		try {
//			String str[] = item.stype.split(":");
//			CommonUtils.showToast("��ǰ״̬:"
//					+ CAR_STATUS[Integer.parseInt(str[str.length - 1])]);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(item.type + "=====");
//		if ("1".equals(item.type)) {
//			findViewById(R.id.car_option).setVisibility(View.VISIBLE);
//			findViewById(R.id.p_options).setVisibility(View.GONE);
//		} else {
//			findViewById(R.id.car_option).setVisibility(View.GONE);
//			findViewById(R.id.p_options).setVisibility(View.VISIBLE);
//		}
//		// if (str == null || str.length < 2) {
//		// CommonUtils.showToast("δ֪״̬");
//		// return;
//		// }
//		// breaks_state();
//		// lock_state();
//		// String states = "";
//		// for (int i = 1; i < str.length; i++) {
//		// int state = Integer.parseInt(str[i]);
//		// switch (state) {
//		// case 0:
//		// unlock_state();
//		// break;
//		// case 1:
//		// lock_state();
//		// break;
//		// case 2:
//		// break;
//		// case 4:
//		// break;
//		// case 5:
//		// break;
//		// case 6:
//		// break;
//		// case 3:
//		// case 7:
//		// case 8:
//		// unbreaks_state();
//		// break;
//		// case 9:
//		// break;
//		// }
//		// states += CAR_STATUS[state] + "  ";
//		// }
//		// TextView tv = (TextView) findViewById(R.id.states);
//		// if ("".equals(states)) {
//		// tv.setText("��ǰ״̬: δ֪");
//		// } else {
//		// tv.setText("��ǰ״̬:" + states);
//		// }
//
//	}
//
//	ProgressDialog mypDialog;
//
//	public void things(View view) {
//		switch (view.getId()) {
//		case R.id.unlock_btn:
//			setUNLock();
//			break;
//		case R.id.unbreak_btn:
//			setunBreaks();
//			break;
//		case R.id.lock_btn:
//			setLock();
//			break;
//		case R.id.break_btn:
//			setBreaks();
//			break;
//		case R.id.back:
//			finish();
//			break;
//		case R.id.monitor:
//			final EditText update_edit = new EditText(this);
//			update_edit.setTextSize(16);
//			update_edit.setInputType(InputType.TYPE_CLASS_PHONE);
//			update_edit.setTextColor(getResources().getColor(
//					android.R.color.black));
//			update_edit.setHint("������Ҫ�����ĵ绰");
//			new AlertDialog.Builder(PeopleControllActivity.this)
//					.setTitle("�����绰")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(update_edit)
//					.setPositiveButton("ȷ��",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO
//									// Auto-generated
//									// method stub
//									String text = update_edit.getText() + "";
//									if ("".equals(text)) {
//										CommonUtils.showToast("������绰����");
//										return;
//									}
//									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//									imm.toggleSoftInput(0,
//											InputMethodManager.HIDE_NOT_ALWAYS);
//									CommonUtils.showProcessDialog(
//											PeopleControllActivity.this,
//											"���ڼ���...");
//									setlisten(update_edit.getText() + "");
//
//								}
//							}).setNegativeButton("ȡ��", null).show();
//
//			break;
//		case R.id.photo_f:
//			// CommonUtils.showProcessDialog(this, "���ڵ���Զ�����...");
//			setPhoto("1");
//			break;
//		case R.id.photo_b:
//			// CommonUtils.showProcessDialog(this, "���ڵ���Զ�����...");
//			// mypDialog = new ProgressDialog(this);
//			// mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			// mypDialog.setMessage("���ڵ���Զ�����...");
//			// mypDialog.setCancelable(false);
//			// mypDialog.setIndeterminate(false);
//			// mypDialog.setCancelable(false);
//			// mypDialog.setButton("��̨����", new DialogInterface.OnClickListener()
//			// {
//			//
//			// @Override
//			// public void onClick(DialogInterface dialog, int which) {
//			// // TODO Auto-generated method stub
//			// mypDialog.dismiss();
//			// }
//			// });
//			// mypDialog.setButton2("����", new DialogInterface.OnClickListener()
//			// {
//			//
//			// @Override
//			// public void onClick(DialogInterface dialog, int which) {
//			// // TODO Auto-generated method stub
//			// mypDialog.dismiss();
//			// }
//			// });
//			// mypDialog.show();
//			setPhoto("2");
//			break;
//		case R.id.relocation:
//			CommonUtils.showProcessDialog(this, "���ڶ�λ...");
//			setLocation();
//			break;
//		case R.id.contactset:
//			// setLocation();
//			final View info = LayoutInflater.from(this).inflate(
//					R.layout.contact_set, null);
//			final EditText number = (EditText) info.findViewById(R.id.number);
//			final EditText name = (EditText) info.findViewById(R.id.name);
//			final EditText index = (EditText) info.findViewById(R.id.index);
//			new AlertDialog.Builder(PeopleControllActivity.this)
//					.setTitle("�����ϵ����Ϣ")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(info)
//					.setPositiveButton("ȷ��",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO
//									// Auto-generated
//									// method stub
//									String name_str = name.getText().toString();
//									String number_str = number.getText()
//											.toString();
//									String index_str = index.getText()
//											.toString();
//									if ("".equals(number_str)) {
//										CommonUtils.showToast("������绰����");
//										return;
//									}
//									if ("".equals(name_str)) {
//										CommonUtils.showToast("��������ϵ������");
//										return;
//									}
//									if ("".equals(index_str)) {
//										CommonUtils.showToast("��������");
//										return;
//									}
//									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//									imm.toggleSoftInput(0,
//											InputMethodManager.HIDE_NOT_ALWAYS);
//									CommonUtils.showProcessDialog(
//											PeopleControllActivity.this,
//											"��������...");
//									setContact(index_str, name_str, number_str);
//
//								}
//							}).setNegativeButton("ȡ��", null).show();
//			break;
//		case R.id.setsafedist:
//			final EditText update_safedist = new EditText(this);
//			update_safedist.setTextSize(16);
//			update_safedist.setTextColor(getResources().getColor(
//					android.R.color.black));
//			update_safedist.setHint("������100~50000");
//			update_safedist.setInputType(InputType.TYPE_CLASS_NUMBER);
//			new AlertDialog.Builder(PeopleControllActivity.this)
//					.setTitle("��ȫ���뱨������")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(update_safedist)
//					.setPositiveButton("ȷ��",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO
//									// Auto-generated
//									// method stub
//									String text = update_safedist.getText()
//											+ "";
//									if ("".equals(text)) {
//										CommonUtils.showToast("���������");
//										return;
//									}
//									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//									imm.toggleSoftInput(0,
//											InputMethodManager.HIDE_NOT_ALWAYS);
//									CommonUtils.showProcessDialog(
//											PeopleControllActivity.this,
//											"��������...");
//									setSafedist(update_safedist.getText() + "");
//
//								}
//							}).setNegativeButton("ȡ��", null).show();
//
//			break;
//		case R.id.sethousing:
//			final View info_content = LayoutInflater.from(this).inflate(
//					R.layout.house_set, null);
//			final EditText number_house = (EditText) info_content
//					.findViewById(R.id.number);
//			final EditText name_house = (EditText) info_content
//					.findViewById(R.id.name);
//			new AlertDialog.Builder(PeopleControllActivity.this)
//					.setTitle("����ڼҳ�ʱ��������")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(info_content)
//					.setPositiveButton("ȷ��",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO
//									// Auto-generated
//									// method stub
//									String name_str = name_house.getText()
//											.toString();
//									String number_str = number_house.getText()
//											.toString();
//									if ("".equals(number_str)) {
//										CommonUtils.showToast("���ݲ���Ϊ��");
//										return;
//									}
//									if ("".equals(name_str)) {
//										CommonUtils.showToast("���ݲ���Ϊ��");
//										return;
//									}
//									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//									imm.toggleSoftInput(0,
//											InputMethodManager.HIDE_NOT_ALWAYS);
//									CommonUtils.showProcessDialog(
//											PeopleControllActivity.this,
//											"��������...");
//									setHouse(name_str, number_str);
//
//								}
//							}).setNegativeButton("ȡ��", null).show();
//
//			break;
//		case R.id.set_txinfo:
//			final View info_info = LayoutInflater.from(this).inflate(
//					R.layout.toast_set, null);
//			final EditText cont_info = (EditText) info_info
//					.findViewById(R.id.cont);
//			final Button time_info = (Button) info_info.findViewById(R.id.time);
//			final EditText index_info = (EditText) info_info
//					.findViewById(R.id.index);
//			new AlertDialog.Builder(PeopleControllActivity.this)
//					.setTitle("��ʱ��������")
//					.setIcon(android.R.drawable.ic_dialog_info)
//					.setView(info_info)
//					.setPositiveButton("ȷ��",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO
//									// Auto-generated
//									// method stub
//									String cont_str = cont_info.getText()
//											.toString();
//									String time_str = time_info.getText()
//											.toString();
//									String index_str = index_info.getText()
//											.toString();
//									if ("".equals(index_str)) {
//										CommonUtils.showToast("��������");
//										return;
//									}
//									if ("".equals(time_str)) {
//										CommonUtils.showToast("����������");
//										return;
//									}
//									if ("".equals(cont_str)) {
//										CommonUtils.showToast("������������");
//										return;
//									}
//									InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//									imm.toggleSoftInput(0,
//											InputMethodManager.HIDE_NOT_ALWAYS);
//									CommonUtils.showProcessDialog(
//											PeopleControllActivity.this,
//											"��������...");
//									setInfo(index_str, time_str, cont_str);
//
//								}
//							}).setNegativeButton("ȡ��", null).show();
//			break;
//		}
//
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	public void setLocation() {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_location
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("��λ�ɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("��λʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("��λʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setHouse(final String name, final String phone) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_bcs_sethousing
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id + "&timeoutin="
//									+ URLEncoder.encode(name, "utf-8")
//									+ "&timeoutout=" + phone, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("���óɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setContact(final String index, final String name,
//			final String phone) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				param.add(new BasicNameValuePair("name", name));
//				param.add(new BasicNameValuePair("index", index));
//				param.add(new BasicNameValuePair("phone", phone));
//				param.add(new BasicNameValuePair("cid", item.id));
//
//				try {
//					json = MySSLSocketFactory
//							.getinfo(
//									Urls.car_bcs_phonebook
//											+ "?serviceid="
//											+ PrefHelper
//													.getStringData(PrefHelper.P_USER_ID),
//									param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("���óɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setInfo(final String index, final String time,
//			final String content) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				param.add(new BasicNameValuePair("index", index));
//				param.add(new BasicNameValuePair("cid", item.id));
//				param.add(new BasicNameValuePair("time", time));
//				param.add(new BasicNameValuePair("cont", content));
//
//				try {
//					json = MySSLSocketFactory
//							.getinfo(
//									Urls.car_bcs_setalarm
//											+ "?serviceid="
//											+ PrefHelper
//													.getStringData(PrefHelper.P_USER_ID),
//									param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("���óɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setSafedist(final String lenght) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_bcs_setsafedist
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id + "&radius=" + lenght,
//							param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("���óɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setlisten(final String phone) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_bcs_listen
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id + "&phone=" + phone,
//							param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("�����ɹ�");
//						finish();
//					} else {
//						CommonUtils.showToast("����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setPhoto(final String cam) {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_bcs_photo
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id + "&cam=" + cam, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("Զ������ɹ�");
//						setResult(success);
//						finish();
//					} else {
//						CommonUtils.h.post(new Runnable() {
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								if (mypDialog != null && mypDialog.isShowing()) {
//									mypDialog.dismiss();
//								}
//							}
//						});
//						CommonUtils.showToast("Զ������ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.h.post(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							if (mypDialog != null && mypDialog.isShowing()) {
//								mypDialog.dismiss();
//							}
//						}
//					});
//					CommonUtils.showToast("Զ������ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					// CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setLock() {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_lock
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("Զ������ɹ�");
//						finish();
//						// unlock_state();
//					} else {
//						CommonUtils.showToast("Զ�����ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("Զ�����ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setUNLock() {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_unlock
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("Զ�̳����ɹ�");
//						// unlock_state();
//						finish();
//					} else {
//						CommonUtils.showToast("Զ�̳���ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("Զ�̳���ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setBreaks() {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_break
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("Զ�̶��͵�ɹ�");
//						finish();
//						// unlock_state();
//					} else {
//						CommonUtils.showToast("Զ�̶��͵�ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("Զ�̶��͵�ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
//
//	public void setunBreaks() {
//		new Thread() {
//			public void run() {
//				List<NameValuePair> param = new ArrayList<NameValuePair>();
//				String json = "";
//				try {
//					json = MySSLSocketFactory.getinfo(
//							Urls.car_unbreak
//									+ "?serviceid="
//									+ PrefHelper
//											.getStringData(PrefHelper.P_USER_ID)
//									+ "&cid=" + item.id, param);
//					JSONObject jsonobj = new JSONObject(json);
//					if (jsonobj.has("success") && jsonobj.getBoolean("success")) {
//						CommonUtils.showToast("Զ�ָ̻��͵�ɹ�");
//						// unlock_state();
//						finish();
//					} else {
//						CommonUtils.showToast("Զ�ָ̻��͵�ʧ��");
//					}
//				} catch (Exception e) {
//					CommonUtils.showToast("Զ�ָ̻��͵�ʧ��");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					CommonUtils.dismissProcessDialog();
//				}
//			};
//		}.start();
//	}
// }

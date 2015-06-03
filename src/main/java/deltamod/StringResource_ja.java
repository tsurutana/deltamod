package deltamod;

import java.util.ListResourceBundle;

/**
 * String resource (Japanese)
 * @author tsuruta
 *
 */

public class StringResource_ja extends ListResourceBundle {
	static final Object[][] strings = { 
		{ "Title", "DeltaMod 1.0" }, 
		{ "File", "ファイル" }, 
		{ "New", "新規作成" }, 
		{ "Open", "開く..." }, 
		{ "Save", "保存..." }, 
		{ "SaveAs", "名前を付けて保存..." }, 
		{ "Export", "エクスポート..."},
		{ "Exit", "終了" }, 

		{ "Edit", "編集" }, 

		{ "View", "表示" },
		{ "Reset", "カメラを初期状態に戻す" },
		{ "Flat", "面のみ" },
		{ "FlatLines", "面と枠線" },
		{ "Wireframe", "ワイヤフレーム" },

		{ "Options", "オプション"},
		
		{ "Optimization", "最適化"},
		{ "Optimize", "最適化"},
		{ "AcceptableError", "許容誤差"},
		{ "NumOfIteration", "反復回数"},
		{ "OptimizeDescription", "<html>最適化処理は辺の長さを一定にします.<br>" +
		"これにより, 入力された三角形メッシュを持つ多面体は, デルタ多面体に変形されます.<br>"},
		{ "LoadReference", "参照モデルの読み込み"},
		
		{ "Help", "ヘルプ" }, 
		{ "Version", "バージョン情報"},

		{ "DialogTitle_FileSave", "名前を付けて保存"},
		{ "Warning_SameNameFileExist", "同名ファイルが存在します。上書きしてもいいですか?"},
		{ "Error_FileSaveFaild", "ファイルの保存に失敗しました。"},
		{ "Manual", "回転: Ctrl + ドラッグ, 平行移動: Shift + ドラッグ, 編集操作の実行: Ctrl + X"},

		{ "Connect", "連結"},
		{ "AddTetra", "正四面体"},
		{ "AddOcta", "正八面体"},
		{ "AddIcosa", "正二十面体"},
		{ "AddModel", "他のデルタ多面体"},
		{ "Load", "開く..."},
		{ "Subtract", "内側に連結する"},
		{ "ConnectDescription", "<html>選択した面に別のデルタ多面体を連結します.<br>" +
			"正多面体を除き, 連結する面を1つだけ選択する必要があります.<br>"},
		
		{ "Elongate", "引き伸ばし(角柱)"},
		{ "Gyroelongate", "ねじり引き伸ばし(反角柱)"},
		{ "ElongateDescription", "<html>選択された角錐部位を引き伸ばします.<br>" +
				"(角柱/反角柱が選択された部位の下に挿入されます.)"},
		
		{ "Tuck", "押し込み"},
		{ "TuckDescription", "<html>選択された角錐部位を押し込む, または引き出します."},
		
		{ "Fill", "多角形の補完"},
		{ "FillQuads", "四角形"},
		{ "FillPentagons", "五角形"},
		{ "FillHexagons", "六角形"},
		{ "FillInside", "内側に連結する"},
		{ "FillDescription", "<html>選択されたN角形(N>3)の面を正三角形で埋めます.<br>" +
				"N=4,5の場合は, N角錐を連結することに相当します."},
		
		{ "Divide", "細分割"},
		{ "NumOfDivision", "分割数 : "},
		{ "DivDescription", "<html>各辺を N 等分します.<br>" +
			"つまり, 各面は N^2 個の三角形に分割されることになります.<br>" +
			"(編集中の多面体はデルタ多面体である必要があります.)"},
		
		{ "Operation", "操作"},
		
		{ "SelectAll", "すべての面を選択 (Ctrl+A)"},
		{ "UnselectAll", "選択を解除 (ESC)"},
		{ "GeomToOrig", "モデルの重心を原点に移動"},
		
		{ "C4", "正四面体"},
		{ "C6", "双三角錐"},
		{ "C8", "正八面体"},
		{ "C10", "双五角錐"},
		{ "C12", "変形双五角錐"},
		{ "C14", "三側錐三角柱"},
		{ "C16", "双四角錐反柱"},
		{ "C20", "正二十面体"},
	};

	protected Object[][] getContents() {
		return strings;
	}
}

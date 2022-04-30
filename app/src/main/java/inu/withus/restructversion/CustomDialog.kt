//package inu.withus.restructversion
//
//class CustomDialog(context: Context, Interface: CustomDialogInterface) : Dialog(context) {
//
//    // 액티비티에서 인터페이스를 받아옴
//    private var customDialogInterface: CustomDialogInterface = Interface
//
//    private lateinit var addButton : Button
//    private lateinit var cancelButton : Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.dialog_custom)
//
//        addButton = findViewById(R.id.add_btn)
//        cancelButton = findViewById(R.id.cancel_btn)
//
//        // 배경을 투명하게함
//        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        // 추가 버튼 클릭 시 onAddButtonClicked 호출 후 종료
//        addButton.setOnClickListener {
//            customDialogInterface.onAddButtonClicked()
//            dismiss()}
//
//        // 취소 버튼 클릭 시 onCancelButtonClicked 호출 후 종료
//        cancelButton.setOnClickListener {
//            customDialogInterface.onCancelButtonClicked()
//            dismiss()}
//    }
//}
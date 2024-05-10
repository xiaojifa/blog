
function checkNotNull(str) {
    return !(str == null || str === "" || str.length < 1);

}

function zuiMsg(msg){
    new $.zui.Messager(msg, {
        type: 'warning',
        placement: 'center'
    }).show();
}

function zuiSuccessMsg(msg){
    new $.zui.Messager(msg, {
        type: 'success',
        placement: 'center'
    }).show();
}
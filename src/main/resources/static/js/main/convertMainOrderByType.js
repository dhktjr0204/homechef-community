const contentOrderTypes=document.querySelectorAll('.content-order-type');
contentOrderTypes.forEach(function (contentOrderType){
    contentOrderType.addEventListener('click',function (e){
        const selectedText=e.target.textContent;
        const selectedValue=e.target.getAttribute("value");
        const orderButton=document.querySelector('.dropbtn');
        orderButton.textContent=textContent=selectedText;
        orderButton.setAttribute('value', selectedValue);

    });
});
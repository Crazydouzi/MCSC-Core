import{d as x,ac as v,r as y,q as I,o as P,b as k,w as e,a as u,e as o,x as d,av as q,Q as b,ae as n}from"./index.9753a940.js";import{$ as g,z as M,g as T}from"./VAvatar.540c951f.js";import{V,b as _}from"./VToolbar.ea7f237a.js";import{f as c,g as h,a as C,c as L,e as N,V as S,n as R}from"./VSpacer.34de78e0.js";import{b as B,a as O,V as f,c as F,e as A}from"./VList.2e30d169.js";import{g as D}from"./forwardRefs.cd29eeea.js";import{V as r}from"./VTextField.20ab6830.js";import{V as z}from"./VSelect.f00e70c4.js";import{V as j}from"./VOverlay.1580b0c3.js";import{V as J}from"./VContainer.5fe4f2fe.js";import"./tag.9461cb4b.js";/* empty css              */import"./VChip.5e379713.js";const Q=n("h4",null,"\u9762\u677F\u7AEF\u53E3",-1),W=n("h4",null,"\u5B57\u7B26\u7F16\u7801",-1),$=n("h4",null,"\u8FDC\u7A0B\u5E93\u914D\u7F6E",-1),G=n("h4",null,"\u6587\u4EF6\u7F13\u5B58\u76EE\u5F55",-1),H=n("p",{style:{"font-size":"medium"}},"\u9ED8\u8BA4\u7F13\u5B58\u4E0A\u4F20\u4F4D\u7F6E",-1),K=n("h4",null,"\u8BF7\u6C42\u5B8C\u6210\u65F6\u91CA\u653E\u7A7A\u95F4",-1),X=n("p",null,"\u5F53\u64CD\u4F5C\u5B8C\u6210\u65F6\u662F\u5426\u81EA\u52A8\u6E05\u7406\u672C\u6B21\u6587\u4EF6\u4E0A\u4F20\u6240\u4EA7\u751F\u7684\u7F13\u5B58\u6587\u4EF6",-1),pu=x({__name:"SystemManager",setup(Y){let s=v({data:{port:null,charset:null,fileOptions:{dir:null,deleteUploadedFilesOnEnd:!1}}});const i=v({data:{user:{id:null,pwd:""}}}),m={required:t=>t?!0:"\u6B64\u9879\u4E0D\u80FD\u4E3A\u7A7A",pwdMin:t=>t.length>=6?!0:"\u8FD9\u5E94\u8BE5\u662F\u4E00\u4E2A\u81F3\u5C116\u4F4D\u7684\u5BC6\u7801",pwdRequired:t=>t===i.data.user.pwd?!0:"\u4E24\u6B21\u5BC6\u7801\u8F93\u5165\u4E0D\u4E00\u81F4"};let p=y(),E=y();function w(){g.request(M.systemConfig).then(t=>{t.code=="200"&&(s.data=t.data)})}function U(){let t=JSON.parse(sessionStorage.getItem("user-sessionData"));i.data.user.id=t.id,g.request(T.changePwd,i.data.user).then(l=>{alert(l.msg),l.code=="200"&&(sessionStorage.clear(),location.reload())})}return I(()=>{w()}),(t,l)=>(P(),k(J,null,{default:e(()=>[u(C,{class:"mt-5"},{default:e(()=>[u(V,{density:"compact",color:"red"},{default:e(()=>[u(_,null,{default:e(()=>[o("\u64CD\u4F5C")]),_:1}),u(c)]),_:1}),u(B),u(h,null,{default:e(()=>[u(D,{variant:"outlined",color:"green"},{default:e(()=>[o("\u4FDD\u5B58\u914D\u7F6E")]),_:1}),u(D,{variant:"outlined",onClick:l[0]||(l[0]=a=>w())},{default:e(()=>[o("\u5237\u65B0")]),_:1})]),_:1})]),_:1}),u(C,{class:"mt-5"},{default:e(()=>[u(V,{density:"compact",color:"red"},{default:e(()=>[u(_,null,{default:e(()=>[o("\u57FA\u672C\u8BBE\u7F6E")]),_:1}),u(c)]),_:1}),u(B),u(L,null,{default:e(()=>[u(N,null,{default:e(()=>[u(S,{cols:"12",lg:"6",xl:"6",md:"6"},{default:e(()=>[u(O,{density:"compact"},{default:e(()=>[u(f,null,{default:e(()=>[u(F,null,{default:e(()=>[Q]),_:1}),u(A,null,{default:e(()=>[o("\u4FEE\u6539\u9762\u677F\u7AEF\u53E3\u9700\u8981\u91CD\u542F\u670D\u52A1\u3002")]),_:1}),u(r,{label:"\u7AEF\u53E3\u53F7",variant:"outlined",class:"mt-2",density:"compact",modelValue:d(s).data.port,"onUpdate:modelValue":l[1]||(l[1]=a=>d(s).data.port=a)},null,8,["modelValue"])]),_:1}),u(f,null,{default:e(()=>[u(F,null,{default:e(()=>[W]),_:1}),u(A,null,{default:e(()=>[o("\u6B64\u9879\u5C06\u5E94\u7528\u4E8E\u7EC8\u7AEF\u7684IO\u5B57\u7B26\u7F16\u7801\uFF0C\u4FEE\u6539\u540E\u9700\u91CD\u542FMC\u670D\u52A1\u5668")]),_:1}),u(r,{label:"\u7F16\u7801",variant:"outlined",class:"mt-2",density:"compact",modelValue:d(s).data.charset,"onUpdate:modelValue":l[2]||(l[2]=a=>d(s).data.charset=a)},null,8,["modelValue"])]),_:1}),u(f,null,{default:e(()=>[u(F,null,{default:e(()=>[$]),_:1}),u(A,null,{default:e(()=>[o("\u6B64\u9879\u5C06\u5E94\u7528\u4E8E\u4ECEWeb\u4E2D\u83B7\u53D6MCServer")]),_:1}),u(r,{variant:"outlined",class:"mt-2",density:"compact",readonly:!0,value:"https://api.papermc.io/v2/projects/paper"})]),_:1})]),_:1})]),_:1}),u(S,null,{default:e(()=>[u(O,null,{default:e(()=>[u(f,null,{default:e(()=>[u(F,null,{default:e(()=>[G]),_:1}),H,u(r,{label:"\u4E0A\u4F20\u4F4D\u7F6E",variant:"outlined",class:"mt-2",density:"compact",modelValue:d(s).data.fileOptions.dir,"onUpdate:modelValue":l[3]||(l[3]=a=>d(s).data.fileOptions.dir=a)},null,8,["modelValue"])]),_:1}),u(f,null,{default:e(()=>[u(F,null,{default:e(()=>[K]),_:1}),X,u(z,{items:[{title:"\u662F",value:!0},{title:"\u5426",value:!1}],variant:"outlined",modelValue:d(s).data.fileOptions.deleteUploadedFilesOnEnd,"onUpdate:modelValue":l[4]||(l[4]=a=>d(s).data.fileOptions.deleteUploadedFilesOnEnd=a),class:"mt-2",density:"compact"},null,8,["modelValue"])]),_:1})]),_:1})]),_:1})]),_:1})]),_:1})]),_:1}),u(C,{class:"mt-5"},{default:e(()=>[u(V,{density:"compact",color:"red"},{default:e(()=>[u(_,null,{default:e(()=>[o("\u4FEE\u6539\u5BC6\u7801")]),_:1}),u(c)]),_:1}),u(B),u(R,null,{default:e(()=>[u(j,{"fast-fail":"",onSubmit:q(U,["prevent"]),modelValue:d(p),"onUpdate:modelValue":l[7]||(l[7]=a=>b(p)?p.value=a:p=a)},{default:e(()=>[u(r,{id:"password",label:"Password",name:"password","model-value":i.data.user.pwd,"onUpdate:modelValue":l[5]||(l[5]=a=>i.data.user.pwd=a),"prepend-icon":"mdi-lock",type:"password",rules:[m.required,m.pwdMin],variant:"underlined"},null,8,["model-value","rules"]),u(r,{id:"checkPassword",label:"\u786E\u8BA4\u5BC6\u7801",name:"checkPassword",modelValue:d(E),"onUpdate:modelValue":l[6]||(l[6]=a=>b(E)?E.value=a:E=a),"prepend-icon":"mdi-lock",type:"password",rules:[m.required,m.pwdMin,m.pwdRequired],variant:"underlined"},null,8,["modelValue","rules"]),u(h,null,{default:e(()=>[u(c),u(D,{type:"submit",variant:"text",disabled:!d(p)},{default:e(()=>[o("\u63D0\u4EA4")]),_:1},8,["disabled"])]),_:1})]),_:1},8,["onSubmit","modelValue"])]),_:1})]),_:1})]),_:1}))}});export{pu as default};

const express = require('express')
const path = require('path')
const app = express()
const url = require('url')
const fs = require('fs')
var bodyParser = require('body-parser')
const superagent = require('superagent')



app.use(express.static(path.join(__dirname, 'public')))
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({//处理以form表单的提交
    extended: true
}))

app.listen(8080, () => {
    console.log('App listening at port 8080')
})

app.get('/news', (req, appres) => {
    console.log(req.url)
    data = url.parse(req.url, true).query
    // console.log(data)
    superagent.get('https://api2.newsminer.net/svc/news/queryNewsList')
        .query({startDate: data.publishTime, endDate: data.publishTime})
        .end((err, res) => {
            // console.log(res.body)
            for (let news of res.body.data) {
                if (news.newsID == data.newsID) {
                    appres.render("index.ejs", news)
                    return
                }
            }
            appres.send("<h1>fail</h1>")
        })
})

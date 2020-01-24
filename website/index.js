var port = process.env.PORT || 8080;
const express = require("express");
const app = express();
app.set("view engine", "ejs");
app.set("views", "./views");
app.use(express.static("assets"));
app.use(express.urlencoded());

// routing
app.get("/", function(req, res) {
  res.render("home.ejs");
  return;
});
app.get("/landmark", function(req, res) {
  res.render("landmark.ejs");
  return;
});
app.listen(port, function(err) {
  if (err) {
    console.log("Server down");
  }
  console.log("Server is up and running");
});

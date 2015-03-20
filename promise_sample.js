function doubleUp(value) {
    return value * 2;
}
function increment(value) {
    return value + 1;
}
function output(value) {
    console.log(value);
}
var promise = new Promise(function (resolve) {
        resolve(1);
});
promise
    .then(increment)
    .then(doubleUp)
    .then(output)
    .catch(function(error){
        console.error(error);
    });

